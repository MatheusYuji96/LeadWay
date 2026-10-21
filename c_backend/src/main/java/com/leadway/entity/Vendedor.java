package com.leadway.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lider_id")
    private Vendedor lider;

    @OneToMany(mappedBy = "lider")
    private List<Vendedor> liderados = new ArrayList<>();

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

    public Vendedor() {
    }

    public Integer getId() {
        return id;
    }

    public String getCodVendedor() {
        return codVendedor;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Vendedor getLider() {
        return lider;
    }

    public void setLider(Vendedor lider) {
        this.lider = lider;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCodVendedor(String codVendedor) {
        this.codVendedor = codVendedor;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    //    public void ativar() {
//        this.ativo = true;
//    }
//
//    public void desativar() {
//        this.ativo = false;
//    }
}
