package com.leadway.dto.request;

import com.leadway.entity.Vendedor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados necessários para atualizar um vendedor no sistema")
public class AtualizarVendedorRequestDTO {

    @Schema(
            description = "Identificador estrangeiro de líder associado ao vendedor",
            example = "2"
    )
    private Integer liderId;

    @Schema(
            description = "E-mail do usuário vendedor",
            example = "mariarocha@gmail.com"
    )
    @Email
    private String email;

    @Schema(
            description = "Senha de acesso do vendedor",
            example = "Senhaalterada123@"
    )
    private String senha;

    @Schema(
            description = "Telefone de contato do usuário vendedor",
            example = "1155855755"
    )
    private String telefone;

    @Schema(
            description = "Status de atividade do usuário vendedor",
            example = "0"
    )
    private Boolean ativo;

    public Integer getLiderId() {
        return liderId;
    }

    public void setLiderId(Integer liderId) {
        this.liderId = liderId;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
