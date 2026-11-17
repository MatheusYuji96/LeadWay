package com.leadway.controller;

import com.leadway.dto.request.CalConectarRequestDTO;
import com.leadway.dto.response.CalIntegracaoResponseDTO;
import com.leadway.service.CalIntegracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Integrações", description = "Conexão do vendedor com o Cal.com")
@RestController
@RequestMapping("/integracoes/calcom")
public class CalIntegracaoController {

    private final CalIntegracaoService calIntegracaoService;

    public CalIntegracaoController(CalIntegracaoService calIntegracaoService) {
        this.calIntegracaoService = calIntegracaoService;
    }

    @Operation(summary = "Estado da conexão com o Cal.com")
    @GetMapping
    public ResponseEntity<CalIntegracaoResponseDTO> status(@RequestParam Integer vendedorId) {
        return ResponseEntity.ok(calIntegracaoService.status(vendedorId));
    }

    @Operation(summary = "Conecta (ou reconecta) o Cal.com",
            description = "Valida a API key no Cal.com e a guarda. A key nunca é devolvida.")
    @PutMapping
    public ResponseEntity<CalIntegracaoResponseDTO> conectar(@RequestBody CalConectarRequestDTO dto) {
        return ResponseEntity.ok(calIntegracaoService.conectar(dto.getVendedorId(), dto.getApiKey()));
    }

    @Operation(summary = "Desconecta o Cal.com",
            description = "Apaga a chave guardada. As reuniões continuam no Cal.com.")
    @DeleteMapping
    public ResponseEntity<Void> desconectar(@RequestParam Integer vendedorId) {
        calIntegracaoService.desconectar(vendedorId);
        return ResponseEntity.noContent().build();
    }
}
