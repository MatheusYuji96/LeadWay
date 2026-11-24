package com.leadway.controller;

import com.leadway.dto.response.CepResponseDTO;
import com.leadway.service.CepService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CEP", description = "Consulta de endereço por CEP (ViaCEP)")
@RestController
@RequestMapping("/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponseDTO> consultar (@PathVariable String cep){
        return ResponseEntity.status(200).body(cepService.consultar(cep));
    }
}
