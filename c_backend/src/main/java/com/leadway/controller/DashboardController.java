package com.leadway.controller;

import com.leadway.dto.response.AtividadeDiariaResponseDTO;
import com.leadway.dto.response.DashboardIndicadoresResponseDTO;
import com.leadway.dto.response.ProximosCompromissosResponseDTO;
import com.leadway.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/indicadores/{id}")
    public ResponseEntity<DashboardIndicadoresResponseDTO> buscarIndicadores(@PathVariable Integer idVendedor) {
        return ResponseEntity.status(200).body(dashboardService.buscarIndicadores(idVendedor));
    }

    @GetMapping("/proximos-compromissos/{id}")
    public ResponseEntity<List<ProximosCompromissosResponseDTO>> buscarProximosCompromissos(@PathVariable Integer idVendedor) {
        return ResponseEntity.status(200).body(dashboardService.buscarProximosCompromissos(idVendedor));
    }

    @GetMapping("/atividades-semana/{id}")
    public  ResponseEntity<AtividadeDiariaResponseDTO> buscarAtividades(@PathVariable Integer idVendedor) {
        return ResponseEntity.status(200).body(dashboardService.buscarAtividade(idVendedor));
    }
}
