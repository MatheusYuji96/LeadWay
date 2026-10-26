package com.leadway.controller;

import com.leadway.dto.request.AtualizarContatoRequestDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Contatos",
        description = "Operações relacionadas ao gerenciamento de contatos dos leads"
)
@RestController
@RequestMapping("/leads/{leadId}/contatos")
public class ContatoController {

    private final ContatoService contatoService;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @Operation(
            summary = "Lista contatos cadastrados no sistema",
            description = "Lista representantes comerciais no sistema, com ou sem filtros."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contatos listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<ContatoResponseDTO>> listarPorFiltro(@RequestParam(required = false) String termo, @RequestParam(required = false) Integer leadId){
        List<Contato> listaContatos = contatoService.listarPorFiltro(termo, leadId);
        List<ContatoResponseDTO> listaResponse = listaContatos.stream().map(contatoMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
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
    public ResponseEntity<ContatoResponseDTO> cadastrar(
            @Valid @RequestBody ContatoRequestDTO dto) {

        Contato contato = contatoService.salvar(dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @Operation(
            summary = "Altera o Lead associado ao contato",
            description = "Altera o id do Lead associado ao contato cadastrado, para caso ele mude de empresa."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato ou Lead não encontrados no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{contatoId}/mudar-lead/{novoLeadId}")
    public ResponseEntity<ContatoResponseDTO> atualizarLead(@PathVariable Integer contatoId, @PathVariable Integer novoLeadId){
        Contato contato = contatoService.renovarLead(contatoId, novoLeadId);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(200).body(responseDTO);
    }

//    @PostMapping
//    public ResponseEntity<ContatoResponseDTO> cadastrar(
//            @PathVariable Integer leadId,
//            @Valid @RequestBody ContatoRequestDTO dto) {
//
//            dto.setLeadId(leadId);
//
//        Contato contato = contatoService.criar(dto);
//        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
//
//        return ResponseEntity.status(201).body(responseDTO);
//    }

    @Operation(
            summary = "Atualiza dados do contato",
            description = "Altera informações relacionadas ao contato, como cargo ou e-mail."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail já registrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ContatoResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarContatoRequestDTO dto){
        Contato contato = contatoService.renovar(id, dto);
        ContatoResponseDTO responseDTO = contatoMapper.toResponse(contato);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @Operation(
            summary = "Deleta dados do contato",
            description = "Apaga informações relacionadas ao contato."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contato deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        contatoService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
