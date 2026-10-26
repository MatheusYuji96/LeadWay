package com.leadway.controller;

import com.leadway.dto.request.AtualizarVendedorRequestDTO;
import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.VendedorService;
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
        name = "Vendedores",
        description = "Operações relacionadas ao gerenciamento de vendedores"
)
@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @Operation(
            summary = "Lista vendedores cadastrados",
            description = "Lista representantes comerciais no sistema, com ou sem filtros."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedores listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(@RequestParam(required = false) String termo) {
        List<Vendedor> lista = vendedorService.listarPorFiltro(termo);
        List<VendedorResponseDTO> listaResponse = lista.stream().map(VendedorMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    @Operation(
            summary = "Cadastra um novo vendedor",
            description = "Cria um novo representante comercial, podendo ser designado à acompanhar um lead específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos, ausentes ou em formato incorreto"),
            @ApiResponse(responseCode = "404", description = "Vendedor líder não consta no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO) {
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @Operation(
            summary = "Atualiza um vendedor",
            description = "Atualiza uma ou mais informações de um vendedor no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<VendedorResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarVendedorRequestDTO requestDTO) {
        Vendedor vendedor = vendedorService.atualizar(id, requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @Operation(
            summary = "Deleta um vendedor",
            description = "Apaga um vendedor do sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vendedor excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        vendedorService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
