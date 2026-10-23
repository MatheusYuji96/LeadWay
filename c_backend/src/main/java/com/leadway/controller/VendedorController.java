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

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(@RequestParam(required = false) String termo){
        List<Vendedor> lista = vendedorService.listarPorFiltro(termo);
        List<VendedorResponseDTO> listaResponse = lista.stream().map(VendedorMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendedorResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody VendedorRequestDTO requestDTO){
        Vendedor vendedor = vendedorService.atualizar(id, requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(200).body(responseDTO);
    }
}
