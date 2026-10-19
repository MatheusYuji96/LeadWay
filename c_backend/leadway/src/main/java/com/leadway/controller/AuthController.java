package com.leadway.controller;

import com.leadway.dto.request.CadastroVendedorRequestDTO;
import com.leadway.dto.request.LoginRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody CadastroVendedorRequestDTO dto) {

        VendedorResponseDTO vendedor = VendedorMapper.toResponse(
                service.cadastrar(
                        VendedorMapper.toEntity(dto),
                        dto.getConfirmarSenha()
                )
        );

        return ResponseEntity.status(200).body(vendedor);

    }

    @PostMapping("/login")
    public ResponseEntity<VendedorResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO dto) {
        VendedorResponseDTO vendedor = VendedorMapper.toResponse(
                service.autenticar(
                        dto.getEmail(),
                        dto.getSenha()
                )
        );

        return ResponseEntity.status(200).body(vendedor);
    }
}
