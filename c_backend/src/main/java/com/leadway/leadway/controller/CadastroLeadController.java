package com.leadway.leadway.controller;

import com.leadway.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.leadway.dto.response.CadastroLeadResponseDTO;
import com.leadway.leadway.entity.Lead;
import com.leadway.leadway.mapper.CadastroLeadMapper;
import com.leadway.leadway.service.CadastroLeadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class CadastroLeadController {

    private final CadastroLeadService cadastroLeadService;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public CadastroLeadController(CadastroLeadService cadastroLeadService) {
        this.cadastroLeadService = cadastroLeadService;
    }

    @PostMapping
    public ResponseEntity<CadastroLeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO){
        Lead lead = cadastroLeadMapper.toEntity(requestDTO);
        Lead leadEntity = cadastroLeadService.salvar(lead);
        CadastroLeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(leadEntity);
        return ResponseEntity.status(200).body(responseDTO);
    }
}
