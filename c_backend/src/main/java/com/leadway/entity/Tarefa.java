package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "contato_id", nullable = false)
    private Contato contato;

    @ManyToOne
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Vendedor responsavel;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataPrazo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ABERTA','CONCLUIDA','CANCELADA')")
    private StatusTarefa status;

    private LocalDateTime concluidoEm;

    private LocalDateTime lembreteEm;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    protected Tarefa() {
    }

    public Integer getId() {
        return id;
    }

    public Contato getContato() {
        return contato;
    }

    public Lead getLead() {
        return lead;
    }

    public Vendedor getResponsavel() {
        return responsavel;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataPrazo() {
        return dataPrazo;
    }

    public LocalDateTime getConcluidoEm() {
        return concluidoEm;
    }

    public LocalDateTime getLembreteEm() {
        return lembreteEm;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public void setResponsavel(Vendedor responsavel) {
        this.responsavel = responsavel;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataPrazo(LocalDateTime dataPrazo) {
        this.dataPrazo = dataPrazo;
    }

    public void setConcluidoEm(LocalDateTime concluidoEm) {
        this.concluidoEm = concluidoEm;
    }

    public void setLembreteEm(LocalDateTime lembreteEm) {
        this.lembreteEm = lembreteEm;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }
}
