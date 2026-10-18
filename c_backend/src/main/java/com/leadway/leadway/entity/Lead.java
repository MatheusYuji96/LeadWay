package com.leadway.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    @OneToOne
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @OneToOne
    @JoinColumn(name = "vendedor_id")
    private Vendedor vendedor;

    @ManyToOne
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @OneToMany(mappedBy = "lead")
    private List<Contato> contatos = new ArrayList<>();

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

<<<<<<< HEAD:c_backend/src/main/java/com/leadway/leadway/entity/Lead.java
    public Lead() {
=======
    protected Lead() {
>>>>>>> origin/development:c_backend/leadway/src/main/java/com/leadway/entity/Lead.java
    }

    public Integer getId() {
        return id;
    }

    public Setor getSetor() {
        return setor;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public String getPotencial() {
        return potencial;
    }

    public String getFarol() {
        return farol;
    }

    public LocalDateTime getDataPrimeiroContato() {
        return dataPrimeiroContato;
    }

    public LocalDateTime getDataPrimeiraVisita() {
        return dataPrimeiraVisita;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public String getMotivoPerda() {
        return motivoPerda;
    }

    public Boolean getAtivo() {
        return ativo;
<<<<<<< HEAD:c_backend/src/main/java/com/leadway/leadway/entity/Lead.java
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public void adicionarContato(Contato contato) {
        contatos.add(contato);
    }

    public void removerContato(Contato contato) {
        contatos.remove(contato);
    }

    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public void setPotencial(String potencial) {
        this.potencial = potencial;
    }

    public void setFarol(String farol) {
        this.farol = farol;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
=======
>>>>>>> origin/development:c_backend/leadway/src/main/java/com/leadway/entity/Lead.java
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public void adicionarContato(Contato contato) {
        contatos.add(contato);
    }

    public void removerContato(Contato contato) {
        contatos.remove(contato);
    }

    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public void setPotencial(String potencial) {
        this.potencial = potencial;
    }

    public void setFarol(String farol) {
        this.farol = farol;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
