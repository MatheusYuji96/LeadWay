package com.leadway.dto.response;

import com.leadway.entity.Vendedor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VendedorResponseDTO {

    private Vendedor lider;
    private String nome;
    private String telefone;
    private String email;
    private String codVendedor;
    private Boolean ativo;

    public Vendedor getLider() {
        return lider;
    }

    public void setLider(Vendedor lider) {
        this.lider = lider;
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
