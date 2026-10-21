package com.leadway.controller;

import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.VendedorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorMapper vendedorMapper = new VendedorMapper();

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(){
        List<Vendedor> lista = vendedorService.listarVendedor();
        List<VendedorResponseDTO> listaResponse = lista.stream().map(vendedorMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = vendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }
}
