package com.leadway.controller;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.LeadResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> listar(@RequestParam(required = false) String termo, @RequestParam(required = false) Integer vendedorId, @RequestParam(required = false) String fase) {
        List<Lead> lista = leadService.listarPorFiltro(termo, vendedorId, fase);
        List<LeadResponseDTO> listaResponse = lista.stream().map(cadastroLeadMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    @PostMapping
    public ResponseEntity<LeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO) {
        Lead leadEntity = leadService.salvar(requestDTO);
        LeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(leadEntity);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> atualizar(@PathVariable Integer id, @RequestBody AtualizarLeadRequestDTO requestDTO) {
        Lead lead = leadService.atualizar(id, requestDTO);
        LeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(lead);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Integer id) {
        leadService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
