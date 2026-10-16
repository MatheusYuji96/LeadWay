package com.leadway.entity;

import jakarta.persistence.*;

@Entity
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // lider_id

    @Column(nullable = false, length = 30)
    private String codVendedor;

    @Column(nullable = false, length = 190)
    private String nome;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String senha;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodVendedor() {
        return codVendedor;
    }

    public void setCodVendedor(String codVendedor) {
        this.codVendedor = codVendedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
