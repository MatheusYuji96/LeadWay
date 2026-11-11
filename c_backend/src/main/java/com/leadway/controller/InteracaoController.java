package com.leadway.controller;

import com.leadway.dto.request.CadastroInteracaoRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.entity.Interacao;
import com.leadway.mapper.InteracaoMapper;
import com.leadway.service.InteracaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leads/{leadId}/interacoes")
public class InteracaoController {

    private final InteracaoService interacaoService;
    private Integer leadId;

    public InteracaoController(InteracaoService interacaoService) {
        this.interacaoService = interacaoService;
    }

    @PostMapping
    public ResponseEntity<InteracaoResponseDTO> cadastrar(@RequestBody @Valid CadastroInteracaoRequestDTO dto,
                                                          @PathVariable Integer leadId) {

        Interacao interacao = interacaoService.cadastrar(leadId,dto);

        return ResponseEntity.status(201).body(InteracaoMapper.toResponse(interacao));
    }

    @GetMapping
    public ResponseEntity<List<InteracaoResponseDTO>> listar(@PathVariable Integer leadId) {

        List<InteracaoResponseDTO> interacaoes =
                interacaoService.listarPorLead(leadId)
                        .stream()
                        .map(InteracaoMapper::toResponse)
                        .toList();

        return ResponseEntity.status(200).body(interacaoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer leadId,
                                        @PathVariable Integer id) {

        interacaoService.excluir(leadId, id);

        return ResponseEntity.status(204).build();
    }
}
