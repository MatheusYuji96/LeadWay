package com.leadway.leadway.controller;

import com.leadway.leadway.dto.request.VendedorRequestDTO;
import com.leadway.leadway.dto.response.VendedorResponseDTO;
import com.leadway.leadway.entity.Vendedor;
import com.leadway.leadway.mapper.VendedorMapper;
import com.leadway.leadway.service.VendedorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorMapper vendedorMapper = new VendedorMapper();

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = vendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
