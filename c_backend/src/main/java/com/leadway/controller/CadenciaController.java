package com.leadway.controller;

import com.leadway.dto.response.CadenciaResponseDTO;
import com.leadway.entity.Interacao;
import com.leadway.service.CadenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leads")
public class CadenciaController {

    private final CadenciaService cadenciaService;

    public CadenciaController(CadenciaService cadenciaService) {
        this.cadenciaService = cadenciaService;
    }

    @GetMapping("/{id}/cadencia")
    public ResponseEntity<CadenciaResponseDTO> buscarCadencia(@PathVariable Integer id){
        return ResponseEntity.status(200).body(cadenciaService.buscar(id));
    }

}
