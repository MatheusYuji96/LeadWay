package com.leadway.controller;

import com.leadway.dto.request.InteracaoRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.service.InteracaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/leads/{leadId}/interacoes")
public class InteracaoController {
    private final InteracaoService service;

    public InteracaoController(InteracaoService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<InteracaoResponseDTO>> listar(@PathVariable Integer leadId) {
        return ResponseEntity.ok(service.listar(leadId));
    }

    @PostMapping
    public ResponseEntity<InteracaoResponseDTO> criar(@PathVariable Integer leadId,
            @Valid @RequestBody InteracaoRequestDTO dto) {
        return ResponseEntity.status(201).body(service.criar(leadId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InteracaoResponseDTO> atualizar(@PathVariable Integer leadId,
            @PathVariable Integer id, @Valid @RequestBody InteracaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(leadId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer leadId, @PathVariable Integer id) {
        service.excluir(leadId, id);
        return ResponseEntity.noContent().build();
    }
}
