package com.leadway.dto.response;

import com.leadway.entity.Vendedor;
import com.leadway.entity.Vendedor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VendedorResponseDTO {

    @Schema(
            description = "Identificador único do vendedor",
            example = "2"
    )
    private Integer id;

    @Schema(
            description = "Identificador estrangeiro de líder associado ao vendedor",
            example = "1"
    )
    private Vendedor lider;

    @Schema(
            description = "Nome do usuário vendedor",
            example = "Maria Rocha"
    )
    private String nome;

    @Schema(
            description = "Telefone de contato do usuário vendedor",
            example = "1199899799"
    )
    private String telefone;

    @Schema(
            description = "E-mail do usuário vendedor",
            example = "mariarocha@outlook.com"
    )
    private String email;

    @Schema(
            description = "Código de vendedor cadastrado pelo líder de equipe",
            example = "VEN-001"
    )
    private String codVendedor;

    @Schema(
            description = "Status de atividade do usuário vendedor",
            example = "1"
    )
    private Boolean ativo;

    public Integer getId() {
    	return id;
    }

    public void setId(Integer id) {
    	this.id = id;
    }

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
