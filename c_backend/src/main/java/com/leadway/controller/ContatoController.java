package com.leadway.controller;

import com.leadway.dto.request.AtualizarContatoRequestDTO;
import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.dto.response.ContatoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.mapper.ContatoMapper;
import com.leadway.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leads/{leadId}/contatos")
public class ContatoController {

    private final ContatoService contatoService;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponseDTO>> listarPorFiltro(@RequestParam(required = false) String termo, @RequestParam(required = false) Integer leadId){
        List<Contato> listaContatos = contatoService.listarPorFiltro(termo, leadId);
        List<ContatoResponseDTO> listaResponse = listaContatos.stream().map(contatoMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
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

    @PatchMapping("/{id}")
    public ResponseEntity<ContatoResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarContatoRequestDTO dto){
        Contato contato = contatoService.renovar(id, dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        contatoService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
