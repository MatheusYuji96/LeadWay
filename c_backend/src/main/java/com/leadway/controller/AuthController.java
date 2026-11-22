package com.leadway.controller;

import com.leadway.dto.request.CadastroVendedorRequestDTO;
import com.leadway.dto.request.LoginRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.AuthService;
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
        name = "Autorização",
        description = "Operações relacionadas ao cadastro e login do usuário"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final com.leadway.service.SessionService sessions;

    public AuthController(AuthService service, com.leadway.service.SessionService sessions) {
        this.service = service;
        this.sessions = sessions;
    }

    @Operation(
            summary = "Cadastra um usuário",
            description = "Registra informações de um usuário principal no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos, ausentes, formato incorreto ou senhas divergentes"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(
            @Valid @RequestBody CadastroVendedorRequestDTO dto) {

        com.leadway.entity.Vendedor created = service.cadastrar(VendedorMapper.toEntity(dto),dto.getConfirmarSenha());
        VendedorResponseDTO vendedor = VendedorMapper.toResponse(created);
        vendedor.setToken(sessions.issue(created));

        return ResponseEntity.status(201).body(vendedor);

    }

    @Operation(
            summary = "Realiza o login do usuário",
            description = "Realiza a autenticação de um usuário previamente cadastrado no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Erro do cliente"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<VendedorResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO dto) {
        com.leadway.entity.Vendedor logged = service.autenticar(dto.getEmail(),dto.getSenha());
        VendedorResponseDTO vendedor = VendedorMapper.toResponse(logged);
        vendedor.setToken(sessions.issue(logged));

        return ResponseEntity.status(200).body(vendedor);
    }
    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ResponseEntity<VendedorResponseDTO> me(
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(VendedorMapper.toResponse(sessions.require(token)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String token) {
        sessions.revoke(token);
        return ResponseEntity.noContent().build();
    }

}
