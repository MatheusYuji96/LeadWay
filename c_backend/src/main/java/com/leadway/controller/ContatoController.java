package com.leadway.controller;

import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.dto.response.ContatoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.mapper.ContatoMapper;
import com.leadway.service.ContatoService;
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
        name = "Contatos",
        description = "Operações relacionadas ao gerenciamento de contatos dos leads"
)
@RestController
@RequestMapping("/leads")
public class ContatoController {

    private final ContatoService contatoService;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @Operation(
            summary = "Cadastra um novo contato",
            description = "Registra um conjunto de informações de contato no sistema, podendo então ser associado a um lead."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contato cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos, ausentes ou em formato incorreto"),
            @ApiResponse(responseCode = "404", description = "Lead não consta no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de contato já cadastrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<ContatoResponseDTO> cadastrar(@Valid @RequestBody ContatoRequestDTO dto){
        Contato contato = contatoService.salvar(dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
