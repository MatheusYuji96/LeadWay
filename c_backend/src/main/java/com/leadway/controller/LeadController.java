package com.leadway.controller;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.LeadResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Leads",
        description = "Operações relacionadas ao gerenciamento de Leads"
)
@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;
    private final com.leadway.service.SessionService sessions;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public LeadController(LeadService leadService, com.leadway.service.SessionService sessions) {
        this.leadService = leadService;
        this.sessions = sessions;
    }

    @Operation(
            summary = "Lista leads no sistema",
            description = "Exibe os leads registrados no sistema, com ou sem filtro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leads listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> listar(@RequestParam(required = false) String termo, @RequestParam(required = false) Integer vendedorId, @RequestParam(required = false) String fase, @RequestHeader(value="Authorization",required=false) String token) {
        com.leadway.entity.Vendedor actor = sessions.require(token);
        List<Lead> lista = leadService.listarPorFiltro(termo, vendedorId, fase).stream()
            .filter(lead -> lead.getVendedor() != null)
            .filter(lead -> sessions.root(actor).getId().equals(sessions.root(lead.getVendedor()).getId()))
            .filter(lead -> !sessions.role(actor).equals("VENDEDOR") || actor.getId().equals(lead.getVendedor().getId()))
            .filter(lead -> !sessions.role(actor).equals("GERENTE") || actor.getId().equals(lead.getVendedor().getId()) || (lead.getVendedor().getLider() != null && actor.getId().equals(lead.getVendedor().getLider().getId())))
            .toList();
        List<LeadResponseDTO> listaResponse = lista.stream().map(cadastroLeadMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    private void requireLeadAccess(com.leadway.entity.Vendedor actor, Integer leadId) {
        Lead lead = leadService.listarLeads().stream().filter(l -> l.getId().equals(leadId)).findFirst()
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Lead não encontrado"));
        if (lead.getVendedor() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Lead sem responsável autorizado");
        sessions.sameOrg(actor, lead.getVendedor());
        if (sessions.role(actor).equals("VENDEDOR") && !actor.getId().equals(lead.getVendedor().getId()))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Lead fora da carteira");
        if (sessions.role(actor).equals("GERENTE") && !actor.getId().equals(lead.getVendedor().getId()) &&
            (lead.getVendedor().getLider() == null || !actor.getId().equals(lead.getVendedor().getLider().getId())))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Lead fora da equipe");
    }

    @Operation(summary = "Consulta a entrada do Lead na etapa atual do funil")
    @GetMapping("/{id}/funil")
    public ResponseEntity<java.util.Map<String, Object>> consultarFunil(@PathVariable Integer id, @RequestHeader(value="Authorization",required=false) String token) {
        requireLeadAccess(sessions.require(token), id);
        java.time.LocalDateTime entrada = leadService.obterEntradaEtapaAtual(id);
        java.util.Map<String, Object> resposta = new java.util.LinkedHashMap<>();
        resposta.put("enteredStageAt", entrada);
        return ResponseEntity.ok(resposta);
    }

    @Operation(
            summary = "Cadastra leads no sistema",
            description = "Registra as informações de um lead no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lead cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Setor e endereço são obrigatórios"),
            @ApiResponse(responseCode = "404", description = "Setor, etapa ou vendedor não encontrados"),
            @ApiResponse(responseCode = "409", description = "CNPJ ou E-mail já registrados no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<LeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO, @RequestHeader(value="Authorization",required=false) String token) {
        sessions.require(token);
        Lead leadEntity = leadService.salvar(requestDTO);
        LeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(leadEntity);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @Operation(
            summary = "Atualiza um lead",
            description = "Atualiza uma ou mais informações de um lead no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail já registrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarLeadRequestDTO requestDTO, @RequestHeader(value="Authorization",required=false) String token) {
        requireLeadAccess(sessions.require(token), id);
        Lead lead = leadService.atualizar(id, requestDTO);
        LeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(lead);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @Operation(
            summary = "Deleta um lead",
            description = "Apaga o registro de um lead no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lead excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Integer id, @RequestHeader(value="Authorization",required=false) String token) {
        requireLeadAccess(sessions.require(token), id);
        leadService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
