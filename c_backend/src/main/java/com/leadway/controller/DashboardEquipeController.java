package com.leadway.controller;

import com.leadway.dto.response.DashboardEquipeIndicadoresResponseDTO;
import com.leadway.dto.response.DashboardEquipeResponseDTO;
import com.leadway.dto.response.DesempenhoVendedorResponseDTO;
import com.leadway.service.DashboardEquipeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard/equipe")
public class DashboardEquipeController {

    private final DashboardEquipeService dashboardEquipeService;

    public DashboardEquipeController(DashboardEquipeService dashboardEquipeService) {
        this.dashboardEquipeService = dashboardEquipeService;
    }

    @Operation(summary = "Resumo real do acompanhamento da equipe")
    @GetMapping("/indicadores/{liderId}")
    public ResponseEntity<DashboardEquipeIndicadoresResponseDTO> buscarIndicadores(@PathVariable Integer liderId) {
        return ResponseEntity.status(200).body(dashboardEquipeService.buscarIndicadores(liderId));
    }

    @GetMapping("/desempenho/{liderId}")
    public ResponseEntity<List<DesempenhoVendedorResponseDTO>> buscarDesempenho(@PathVariable Integer liderId){
        return ResponseEntity.status(200).body(dashboardEquipeService.buscarDesempenho(liderId));
    }

//    @GetMapping("/leads-parados/{liderId}")
}
