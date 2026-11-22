package com.leadway.controller;

import com.leadway.dto.request.NotaLeadRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.entity.Interacao;
import com.leadway.mapper.InteracaoMapper;
import com.leadway.service.InteracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Anotações",
        description = "Registro de anotações e interações no histórico de um lead"
)
@RestController
@RequestMapping("/leads/{leadId}/notes")
public class NotaController {

    private final InteracaoService interacaoService;

    public NotaController(InteracaoService interacaoService) {
        this.interacaoService = interacaoService;
    }

    @Operation(
            summary = "Registra uma anotação no lead",
            description = "Grava uma interação no histórico do lead. tipo e dataHora são opcionais; "
                    + "quando omitidos, usam ANOTACAO e a data/hora atual. Se etapaId for informado, "
                    + "a etapa do lead é atualizada e o histórico de etapas é registrado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Anotação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Descrição ausente ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Lead, contato ou etapa não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<InteracaoResponseDTO> cadastrar(@PathVariable Integer leadId,
                                                           @Valid @RequestBody NotaLeadRequestDTO dto) {
        Interacao interacao = interacaoService.cadastrarNota(leadId, dto);
        return ResponseEntity.status(201).body(InteracaoMapper.toResponse(interacao));
    }
}
