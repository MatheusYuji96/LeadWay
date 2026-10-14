package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Setor setor;

    @OneToOne
    private Endereco endereco;

    @OneToOne
    private Vendedor vendedor;

    @ManyToOne
    private Etapa etapa;

    @Column(columnDefinition = "CHAR(14)", nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false, length = 190)
    private String razaoSocial;

    @Column(nullable = false, length = 190)
    private String nomeFantasia;

    @Column(nullable = false, length = 255,unique = true)
    private String email;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String telefone;

    @Column(nullable = false, length = 30)
    private String prioridade;

    @Column(nullable = false, length = 30)
    private String potencial;

    @Column(nullable = false, length = 20)
    private String farol;

    private LocalDateTime dataPrimeiroContato;

    private LocalDateTime dataPrimeiraVisita;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(columnDefinition = "TEXT")
    private String motivoPerda;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Lead() {
    }

    public Lead(Integer id, Setor setor, Endereco endereco, Vendedor vendedor, Etapa etapa, String cnpj, String razaoSocial, String nomeFantasia, String email, String telefone, String prioridade, String potencial, String farol, LocalDateTime dataPrimeiroContato, LocalDateTime dataPrimeiraVisita, LocalDateTime criadoEm, LocalDateTime atualizadoEm, String motivoPerda, Boolean ativo) {
        this.id = id;
        this.setor = setor;
        this.endereco = endereco;
        this.vendedor = vendedor;
        this.etapa = etapa;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.email = email;
        this.telefone = telefone;
        this.prioridade = prioridade;
        this.potencial = potencial;
        this.farol = farol;
        this.dataPrimeiroContato = dataPrimeiroContato;
        this.dataPrimeiraVisita = dataPrimeiraVisita;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        this.motivoPerda = motivoPerda;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getPotencial() {
        return potencial;
    }

    public void setPotencial(String potencial) {
        this.potencial = potencial;
    }

    public String getFarol() {
        return farol;
    }

    public void setFarol(String farol) {
        this.farol = farol;
    }

    public LocalDateTime getDataPrimeiroContato() {
        return dataPrimeiroContato;
    }

    public void setDataPrimeiroContato(LocalDateTime dataPrimeiroContato) {
        this.dataPrimeiroContato = dataPrimeiroContato;
    }

    public LocalDateTime getDataPrimeiraVisita() {
        return dataPrimeiraVisita;
    }

    public void setDataPrimeiraVisita(LocalDateTime dataPrimeiraVisita) {
        this.dataPrimeiraVisita = dataPrimeiraVisita;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getMotivoPerda() {
        return motivoPerda;
    }

    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
