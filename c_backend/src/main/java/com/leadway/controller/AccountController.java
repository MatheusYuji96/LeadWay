package com.leadway.controller;

import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.AccountService;
import com.leadway.service.SessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/me")
public class AccountController {
    private final SessionService sessions;
    private final AccountService accounts;
    public AccountController(SessionService sessions, AccountService accounts) {
        this.sessions = sessions;
        this.accounts = accounts;
    }
    public record ProfileUpdate(@NotBlank String nome, @Email @NotBlank String email,
                                @Pattern(regexp="\\d{11}") String telefone) {}
    public record PasswordUpdate(String currentPassword, String newPassword) {}

    @PatchMapping
    public ResponseEntity<VendedorResponseDTO> update(
            @RequestHeader(value="Authorization", required=false) String authorization,
            @Valid @RequestBody ProfileUpdate payload) {
        var actor = sessions.require(authorization);
        return ResponseEntity.ok(VendedorMapper.toResponse(accounts.update(actor, payload.nome(), payload.email(), payload.telefone())));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> password(
            @RequestHeader(value="Authorization", required=false) String authorization,
            @RequestBody PasswordUpdate payload) {
        accounts.changePassword(sessions.require(authorization), payload.currentPassword(), payload.newPassword());
        return ResponseEntity.noContent().build();
    }
}
