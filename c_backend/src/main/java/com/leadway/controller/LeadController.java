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
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
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
    public ResponseEntity<List<LeadResponseDTO>> listar(@RequestParam(required = false) String termo, @RequestParam(required = false) Integer vendedorId, @RequestParam(required = false) String fase) {
        List<Lead> lista = leadService.listarPorFiltro(termo, vendedorId, fase);
        List<LeadResponseDTO> listaResponse = lista.stream().map(cadastroLeadMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
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
    public ResponseEntity<LeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO) {
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
    public ResponseEntity<LeadResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarLeadRequestDTO requestDTO) {
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
    public ResponseEntity<Void> apagar(@PathVariable Integer id) {
        leadService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
