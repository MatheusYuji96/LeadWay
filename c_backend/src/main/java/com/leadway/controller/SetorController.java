package com.leadway.controller;

import com.leadway.dto.response.SetorResponseDTO;
import com.leadway.entity.Setor;
import com.leadway.mapper.SetorMapper;
import com.leadway.service.SetorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setores")
public class SetorController {

    private final SetorService setorService;
    private final SetorMapper setorMapper;

    public SetorController(SetorService setorService, SetorMapper setorMapper) {
        this.setorService = setorService;
        this.setorMapper = setorMapper;
    }

    @GetMapping
    public ResponseEntity<List<SetorResponseDTO>> buscarTodos(){
        List<Setor> lista = setorService.buscar();
        List<SetorResponseDTO> dto = lista.stream().map(setorMapper::toResponse).toList();
        return ResponseEntity.status(200).body(dto);
    }
}
