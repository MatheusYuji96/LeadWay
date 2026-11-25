package com.leadway.controller;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.request.TransferirLeadRequestDTO;
import com.leadway.dto.response.CadenciaResponseDTO;
import com.leadway.dto.response.LeadDetalhadoResponseDTO;
import com.leadway.dto.response.LeadResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.service.CadenciaService;
import com.leadway.service.LeadDetalheService;
import com.leadway.service.LeadService;
import com.leadway.service.SessionService;
import com.leadway.service.VendedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Leads",
        description = "Operações relacionadas ao gerenciamento de Leads"
)
@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;
    private final LeadDetalheService leadDetalheService;
    private final CadenciaService cadenciaService;
    private final VendedorService vendedorService;
    private final SessionService sessions;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public LeadController(LeadService leadService,
                          LeadDetalheService leadDetalheService,
                          CadenciaService cadenciaService,
                          VendedorService vendedorService,
                          SessionService sessions) {
        this.leadService = leadService;
        this.leadDetalheService = leadDetalheService;
        this.cadenciaService = cadenciaService;
        this.vendedorService = vendedorService;
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
    public ResponseEntity<List<LeadResponseDTO>> listar(@RequestParam(required = false) String termo,
                                                        @RequestParam(required = false) Integer vendedorId,
                                                        @RequestParam(required = false) String fase,
                                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        List<Lead> lista = leadService.listarPorFiltro(termo, vendedorId, fase).stream()
                .filter(lead -> lead.getVendedor() != null)
                .filter(lead -> sessions.root(actor).getId().equals(sessions.root(lead.getVendedor()).getId()))
                .filter(lead -> !sessions.role(actor).equals("VENDEDOR") || actor.getId().equals(lead.getVendedor().getId()))
                .filter(lead -> !sessions.role(actor).equals("GERENTE") || actor.getId().equals(lead.getVendedor().getId())
                        || (lead.getVendedor().getLider() != null && actor.getId().equals(lead.getVendedor().getLider().getId())))
                .toList();
        return ResponseEntity.ok(lista.stream().map(cadastroLeadMapper::toResponse).toList());
    }

    @Operation(
            summary = "Detalha um lead",
            description = "feature/calcom: agregado com contatos, timeline, responsável, funil e cadência."
    )
    @GetMapping("/{id}")
    public ResponseEntity<LeadDetalhadoResponseDTO> listarPorId(@PathVariable Integer id,
                                                                @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), id);
        return ResponseEntity.ok(leadDetalheService.buscar(id));
    }

    @Operation(summary = "Consulta a entrada do Lead na etapa atual do funil")
    @GetMapping("/{id}/funil")
    public ResponseEntity<Map<String, Object>> consultarFunil(@PathVariable Integer id,
                                                              @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), id);
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("enteredStageAt", leadService.obterEntradaEtapaAtual(id));
        return ResponseEntity.ok(resposta);
    }

    /**
     * Rota antiga do feature/calcom preservada como compatibilidade. O contrato canônico
     * do front unificado é /cadencias (CadenciaController, feature/teste-all).
     */
    @Operation(summary = "Cadência resumida do lead (compatibilidade)")
    @GetMapping("/{id}/cadencia")
    public ResponseEntity<CadenciaResponseDTO> buscarCadencia(@PathVariable Integer id,
                                                              @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), id);
        return ResponseEntity.ok(cadenciaService.buscar(id));
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
    public ResponseEntity<LeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO,
                                                     @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.require(token);
        Lead leadEntity = leadService.salvar(requestDTO);
        return ResponseEntity.status(201).body(cadastroLeadMapper.toResponse(leadEntity));
    }

    @Operation(
            summary = "Transfere o responsável de um lead",
            description = "feature/calcom: altera o vendedor responsável e registra a operação no log."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Responsável transferido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou vendedor inativo"),
            @ApiResponse(responseCode = "404", description = "Lead ou vendedor não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transferirLead(@PathVariable Integer id,
                                               @Valid @RequestBody TransferirLeadRequestDTO requestDTO,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        sessions.requireManager(actor);
        sessions.requireLeadAccess(actor, id);
        sessions.sameOrg(actor, vendedorService.buscar(requestDTO.getVendedorId()));
        leadService.transferir(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualiza um lead",
            description = "Atualiza uma ou mais informações de um lead no sistema."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> atualizar(@PathVariable Integer id,
                                                     @Valid @RequestBody AtualizarLeadRequestDTO requestDTO,
                                                     @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), id);
        Lead lead = leadService.atualizar(id, requestDTO);
        return ResponseEntity.ok(cadastroLeadMapper.toResponse(lead));
    }

    @Operation(summary = "Desativa um lead")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Integer id,
                                       @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), id);
        leadService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
