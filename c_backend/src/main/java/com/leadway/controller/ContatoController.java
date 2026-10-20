package com.leadway.controller;

import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.dto.response.ContatoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.mapper.ContatoMapper;
import com.leadway.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leads/{leadId}/contatos")
public class ContatoController {

    private final ContatoService contatoService;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @PostMapping
    public ResponseEntity<ContatoResponseDTO> cadastrar(
            @PathVariable Integer leadId,
            @Valid @RequestBody ContatoRequestDTO dto) {

        dto.setLeadId(leadId);

        Contato contato = contatoService.salvar(dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);

        return ResponseEntity.status(201).body(responseDTO);
    }
}
