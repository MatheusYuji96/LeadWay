package com.leadway.controller;

import com.leadway.dto.response.EtapaResponseDTO;
import com.leadway.entity.Etapa;
import com.leadway.mapper.EtapaMapper;
import com.leadway.service.EtapaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etapas")
public class EtapaController {

    private final EtapaService etapaService;
    private final EtapaMapper etapaMapper;

    public EtapaController(EtapaService etapaService, EtapaMapper etapaMapper) {
        this.etapaService = etapaService;
        this.etapaMapper = etapaMapper;
    }

    @GetMapping
    public ResponseEntity<List<EtapaResponseDTO>> buscar(){
        List<Etapa> lista = etapaService.buscarTodos();
        List<EtapaResponseDTO> dto = lista.stream().map(etapaMapper::toResponse).toList();
        return ResponseEntity.status(200).body(dto);
    }
}
