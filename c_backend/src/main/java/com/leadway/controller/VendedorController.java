package com.leadway.controller;

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

    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(@RequestParam(required = false) String termo){
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
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "404", description = "Vendedor líder não consta no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema")
    })
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
