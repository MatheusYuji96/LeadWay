package com.leadway.dto.response;

import java.time.LocalDateTime;

public class LeadAtencaoResponseDTO {

    private Integer leadId;
    private String nomeLead;
    private String etapa;
    private LocalDateTime ultimoContato;
    private String proximaAcao;
    private LocalDateTime dataProximaAcao;

    public LeadAtencaoResponseDTO(
            Integer leadId,
            String nomeLead,
            String etapa,
            LocalDateTime ultimoContato,
            String proximaAcao,
            LocalDateTime dataProximaAcao) {
        this.leadId = leadId;
        this.nomeLead = nomeLead;
        this.etapa = etapa;
        this.ultimoContato = ultimoContato;
        this.proximaAcao = proximaAcao;
        this.dataProximaAcao = dataProximaAcao;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public String getNomeLead() {
        return nomeLead;
    }

    public void setNomeLead(String nomeLead) {
        this.nomeLead = nomeLead;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public LocalDateTime getUltimoContato() {
        return ultimoContato;
    }

    public void setUltimoContato(LocalDateTime ultimoContato) {
        this.ultimoContato = ultimoContato;
    }

    public String getProximaAcao() {
        return proximaAcao;
    }

    public void setProximaAcao(String proximaAcao) {
        this.proximaAcao = proximaAcao;
    }

    public LocalDateTime getDataProximaAcao() {
        return dataProximaAcao;
    }

    public void setDataProximaAcao(LocalDateTime dataProximaAcao) {
        this.dataProximaAcao = dataProximaAcao;
    }
}
