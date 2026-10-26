package com.leadway.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para o login do usuário vendedor no sistema")
public class LoginRequestDTO {

    @Schema(
            description = "E-mail solicitado no login do usuário vendedor",
            example = "mariarocha@outlook.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Senha solicitada no login do usuário vendedor",
            example = "Senhamuitosegura123!"
    )
    @NotBlank
    private String senha;

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public @NotBlank String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank String senha) {
        this.senha = senha;
    }
}
