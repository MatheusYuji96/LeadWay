package com.leadway.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroVendedorRequestDTO {

    @NotBlank
    @Size(min = 3)
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    @Pattern(
            regexp = "^(?=.[A-Za-z])(?=.\\d)(?=.*[^A-Za-z\\d\\s])\\S+$",
            message = "A senha deve conter letras, números e caracteres especiais"
    )
    private String senha;

    @NotBlank
    private String confirmarSenha;

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
