package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_etapa_historico")
public class LeadEtapaHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @ManyToOne
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected LeadEtapaHistorico() {
    }

    public Integer getId() {
        return id;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public Lead getLead() {
        return lead;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }
}
