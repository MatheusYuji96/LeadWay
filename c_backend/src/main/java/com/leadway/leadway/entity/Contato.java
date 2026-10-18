package com.leadway.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(nullable = false, length = 190)
    private String nome;

    @Column(nullable = false, length = 45)
    private String cargo;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(length = 11)
    private String telefone;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String whatsapp;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PADRAO', 'INFLUENCIADOR', 'DECISOR')")
    private ClassificacaoContato classificacao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(nullable = false)
    private Boolean ativo = true;

    protected Contato() {
    }

    public Integer getId() {
        return id;
    }

    public Lead getLead() {
        return lead;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public ClassificacaoContato getClassificacao() {
        return classificacao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public void setClassificacao(ClassificacaoContato classificacao) {
        this.classificacao = classificacao;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}
