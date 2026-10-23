package com.leadway.controller;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.CadastroLeadResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.service.CadastroLeadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leads")
public class CadastroLeadController {

    private final CadastroLeadService cadastroLeadService;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public CadastroLeadController(CadastroLeadService cadastroLeadService) {
        this.cadastroLeadService = cadastroLeadService;
    }

    @GetMapping
    public ResponseEntity<List<CadastroLeadResponseDTO>> listar(@RequestParam(required = false) String termo, @RequestParam(required = false) Long vendedorId, @RequestParam(required = false) String cadencia, @RequestParam(required = false) String fase){
        List<Lead> lista = cadastroLeadService.listarPorFiltro(termo, vendedorId, cadencia, fase);
        List<CadastroLeadResponseDTO> listaResponse = lista.stream().map(cadastroLeadMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    @PostMapping
    public ResponseEntity<CadastroLeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO){
        Lead leadEntity = cadastroLeadService.salvar(requestDTO);
        CadastroLeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(leadEntity);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CadastroLeadResponseDTO> atualizar(@PathVariable Integer id, @RequestBody CadastroLeadRequestDTO requestDTO){
        Lead lead = cadastroLeadService.atualizar(id, requestDTO);
        CadastroLeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(lead);
        return ResponseEntity.status(200).body(responseDTO);
    }
}
