package com.leadway.leadway.controller;

import com.leadway.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.leadway.dto.response.CadastroLeadResponseDTO;
import com.leadway.leadway.entity.Lead;
import com.leadway.leadway.mapper.CadastroLeadMapper;
import com.leadway.leadway.service.CadastroLeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Leads",
        description = "Operações relacionadas ao gerenciamento de leads"
)
@RestController
@RequestMapping("/leads")
public class CadastroLeadController {

    private final CadastroLeadService cadastroLeadService;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public CadastroLeadController(CadastroLeadService cadastroLeadService) {
        this.cadastroLeadService = cadastroLeadService;
    }

    @Operation(
            summary = "Cadastra um novo Lead",
            description = "Cria um novo possível cliente no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lead cadastrado com sucesso")
            // procurar forma de registrar os outros 404
    })
    @PostMapping
    public ResponseEntity<CadastroLeadResponseDTO> cadastrar(@Valid @RequestBody CadastroLeadRequestDTO requestDTO){
        Lead leadEntity = cadastroLeadService.salvar(requestDTO);
        CadastroLeadResponseDTO responseDTO = cadastroLeadMapper.toResponse(leadEntity);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
