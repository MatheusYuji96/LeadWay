package com.leadway.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastrar um usuário vendedor no sistema")
public class CadastroVendedorRequestDTO {

    @Schema(
            description = "Nome do usuário vendedor",
            example = "Maria Rocha"
    )
    @NotBlank
    @Size(min = 3)
    private String nome;

    @Schema(
            description = "E-mail do usuário vendedor",
            example = "mariarocha@outlook.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Senha utilizada no cadastro e login do usuário",
            example = "Senhamuitosegura123!"
    )
    @NotBlank
    @Size(min = 8)
    @Pattern(
            regexp = "^(?=.[A-Za-z])(?=.\\d)(?=.*[^A-Za-z\\d\\s])\\S+$",
            message = "A senha deve conter letras, números e caracteres especiais"
    )
    private String senha;

    @Schema(
            description = "Confirmação de senha utilizada no cadastro e login do usuário",
            example = "Senhamuitosegura123!"
    )
    @NotBlank
    private String confirmarSenha;

    @Schema(
            description = "Telefone utilizado pelo usuário vendedor",
            example = "1199899799"
    )
    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]+$")
    private String telefone;

    public @NotBlank @Size(min = 3) String getNome() {
        return nome;
    }

    public void setNome(@NotBlank @Size(min = 3) String nome) {
        this.nome = nome;
    }

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 8) @Pattern(
            regexp = "^(?=.[A-Za-z])(?=.\\d)(?=.*[^A-Za-z\\d\\s])\\S+$",
            message = "A senha deve conter letras, números e caracteres especiais"
    ) String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank @Size(min = 8) @Pattern(
            regexp = "^(?=.[A-Za-z])(?=.\\d)(?=.*[^A-Za-z\\d\\s])\\S+$",
            message = "A senha deve conter letras, números e caracteres especiais"
    ) String senha) {
        this.senha = senha;
    }

    public @NotBlank String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(@NotBlank String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public @NotBlank @Size(min = 11, max = 11) @Pattern(regexp = "^[0-9]+$") String getTelefone() {
        return telefone;
    }

    public void setTelefone(@NotBlank @Size(min = 11, max = 11) @Pattern(regexp = "^[0-9]+$") String telefone) {
        this.telefone = telefone;
    }
}
