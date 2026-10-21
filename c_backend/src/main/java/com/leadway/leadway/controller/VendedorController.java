package com.leadway.leadway.controller;

import com.leadway.leadway.dto.request.VendedorRequestDTO;
import com.leadway.leadway.dto.response.VendedorResponseDTO;
import com.leadway.leadway.entity.Vendedor;
import com.leadway.leadway.mapper.VendedorMapper;
import com.leadway.leadway.service.VendedorService;
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
        name = "Vendedores",
        description = "Operações relacionadas ao gerenciamento de vendedores"
)
@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorMapper vendedorMapper = new VendedorMapper();

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @Operation(
            summary = "Cadastra um novo vendedor",
            description = "Cria um novo representante comercial, podendo ser designado à acompanhar um lead específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor cadastrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor líder não consta no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema")
    })
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = vendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
