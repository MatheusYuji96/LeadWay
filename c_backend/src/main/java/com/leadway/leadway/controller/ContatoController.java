package com.leadway.leadway.controller;

import com.leadway.leadway.dto.request.ContatoRequestDTO;
import com.leadway.leadway.dto.response.ContatoResponseDTO;
import com.leadway.leadway.entity.Contato;
import com.leadway.leadway.mapper.ContatoMapper;
import com.leadway.leadway.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leads")
public class ContatoController {

    private final ContatoService contatoService;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @PostMapping
    public ResponseEntity<ContatoResponseDTO> cadastrar(@Valid @RequestBody ContatoRequestDTO dto){
        Contato contato = contatoService.salvar(dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
