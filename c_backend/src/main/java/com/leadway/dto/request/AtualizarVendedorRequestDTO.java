package com.leadway.dto.request;

import com.leadway.entity.Vendedor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

public class AtualizarVendedorRequestDTO {

    private Integer liderId;

    @Email
    private String email;

    private String senha;
    private String telefone;
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
