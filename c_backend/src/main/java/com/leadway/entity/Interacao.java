package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Interacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "contato_id")
    private Contato contato;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(nullable = false, length = 45)
    private String tipo;

    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

    public Interacao() {
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

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
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

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
