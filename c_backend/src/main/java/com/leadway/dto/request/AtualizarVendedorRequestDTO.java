package com.leadway.dto.request;

import com.leadway.entity.Vendedor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados necessários para atualizar um vendedor no sistema")
public class AtualizarVendedorRequestDTO {

    @Schema(
            description = "Identificador estrangeiro de líder associado ao vendedor",
            example = "2"
    )
    private Integer liderId;
    @Schema(description = "Novo cargo: GERENTE ou VENDEDOR")
    @Pattern(regexp = "GERENTE|VENDEDOR", message = "Cargo inválido")
    private String cargo;
    public String getCargo(){return cargo;}
    public void setCargo(String cargo){this.cargo=cargo;}
    @Size(max=190) private String nome;
    @Size(max=30) private String codVendedor;
    @Size(max=100) private String regiao;
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getCodVendedor(){return codVendedor;} public void setCodVendedor(String v){codVendedor=v;}
    public String getRegiao(){return regiao;} public void setRegiao(String v){regiao=v;}

    @Schema(
            description = "E-mail do usuário vendedor",
            example = "mariarocha@gmail.com"
    )
    @Email
    @Size(max = 255)
    private String email;

    @Schema(
            description = "Senha de acesso do vendedor",
            example = "Senhaalterada123@"
    )
    @Size(max = 50)
    private String senha;

    @Schema(
            description = "Telefone de contato do usuário vendedor",
            example = "1155855755"
    )
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]+$")
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
