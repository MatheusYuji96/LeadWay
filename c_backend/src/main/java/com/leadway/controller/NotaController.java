package com.leadway.controller;

import com.leadway.dto.request.NotaLeadRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.service.InteracaoService;
import com.leadway.service.SessionService;
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
    private final SessionService sessions;

    public NotaController(InteracaoService interacaoService, SessionService sessions) {
        this.interacaoService = interacaoService;
        this.sessions = sessions;
    }

    @Operation(
            summary = "Registra uma anotação no lead",
            description = "feature/calcom: tipo e dataHora opcionais (ANOTACAO / agora). "
                    + "Se etapaId vier preenchido, a etapa do lead é atualizada com histórico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Anotação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Descrição ausente ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Lead, contato ou etapa não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<InteracaoResponseDTO> cadastrar(@PathVariable Integer leadId,
                                                          @Valid @RequestBody NotaLeadRequestDTO dto,
                                                          @RequestHeader(value = "Authorization", required = false) String token) {
        sessions.requireLeadAccess(sessions.require(token), leadId);
        // O mapper de interação foi removido no feature/teste-all: o serviço devolve o record canônico.
        return ResponseEntity.status(201).body(interacaoService.cadastrarNota(leadId, dto));
    }
}
