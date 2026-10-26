package com.leadway.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para cadastrar um representante comercial no sistema, supervisionado por um usuário principal/líder de equipe no sistema")
public class VendedorRequestDTO {

    @Schema(
            description = "Identificador estrangeiro do vendedor responsável pelo Lead",
            example = "2"
    )
    private Integer liderId;

    @Schema(
            description = "Nome do representante comercial",
            example = "David"
    )
    @NotBlank
    private String nome;

    @Schema(
            description = "Telefone de contato do representante comercial",
            example = "1177977877"
    )
    @NotBlank
    private String telefone;

    @Schema(
            description = "E-mail do usuário vendedor",
            example = "mariarocha@outlook.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Senha utilizada no cadastro e login do representante comercial",
            example = "Urubu100!"
    )
    @NotBlank
    private String senha;

    @Schema(
            description = "Código de vendedor cadastrado pelo líder de equipe",
            example = "VEN-001"
    )
    @NotBlank
    private String codVendedor;

    @Schema(
            description = "Status de atividade do usuário vendedor",
            example = "1"
    )
    @NotBlank
    private Boolean ativo;

    public Integer getLiderId() {
        return liderId;
    }

    public void setLiderId(Integer liderId) {
        this.liderId = liderId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCodVendedor() {
        return codVendedor;
    }

    public void setCodVendedor(String codVendedor) {
        this.codVendedor = codVendedor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
