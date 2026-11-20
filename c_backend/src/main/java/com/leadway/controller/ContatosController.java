package com.leadway.controller;

import com.leadway.dto.response.ContatoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.mapper.ContatoMapper;
import com.leadway.service.ContatosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContatosController {

    private final ContatosService contatosService;
    private final ContatoMapper contatoMapper;

    public ContatosController(ContatosService contatosService, ContatoMapper contatoMapper) {
        this.contatosService = contatosService;
        this.contatoMapper = contatoMapper;
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponseDTO>> buscar(){
        List<Contato> lista = contatosService.buscarTodos();
        List<ContatoResponseDTO> dto = lista.stream().map(contatoMapper::toResponse).toList();
        return ResponseEntity.status(200).body(dto);
    }
}
