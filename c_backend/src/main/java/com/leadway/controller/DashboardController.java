package com.leadway.controller;

import com.leadway.dto.response.*;
import com.leadway.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Dashboard",
        description = "Operações relacionadas ao gerenciamento da dashboard"
)
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
            summary = "Busca indicadores gerais da Dashboard",
            description = "Exibe as informações gerais da Dashboard de forma detalhada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicadores buscados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/indicadores/{id}")
    public ResponseEntity<DashboardIndicadoresResponseDTO> buscarIndicadores(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(dashboardService.buscarIndicadores(id));
    }

    @Operation(
            summary = "Busca próximos compromissos",
            description = "Exibe compromissos próximos a acontecer na Dashboard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compromissos buscados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/proximos-compromissos/{id}")
    public ResponseEntity<List<ProximosCompromissosResponseDTO>> buscarProximosCompromissos(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(dashboardService.buscarProximosCompromissos(id));
    }

    @Operation(
            summary = "Busca atividades no sistema",
            description = "Exibe as atividades programadas pelo usuário na dashboard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atividades buscadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/atividades-semana/{id}")
    public ResponseEntity<AtividadesSemanaResponseDTO> buscarAtividades(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(dashboardService.buscarAtividadesSemana(id));
    }

    @Operation(
            summary = "Busca leads em estado de atenção",
            description = "Exibe leads em risco de perda de oportunidade na dashboard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leads em risco buscados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/leads-atencao/{id}")
    public ResponseEntity<List<LeadAtencaoResponseDTO>> buscarLeadsAtencao(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(dashboardService.buscarLeadsAtencao(id));
    }
}