package com.leadway.dto.response;

import java.time.LocalDateTime;

public class LeadParadoResponseDTO {

    private Integer leadId;
    private String nomeLead;
    private Integer vendedorId;
    private String nomeVendedor;
    private String etapa;
    private LocalDateTime ultimaAtividade;
    private Integer diasParados;

    public LeadParadoResponseDTO() {
    }

    public LeadParadoResponseDTO(Integer leadId, String nomeLead, Integer vendedorId, String nomeVendedor, String etapa, LocalDateTime ultimaAtividade, Integer diasParados) {
        this.leadId = leadId;
        this.nomeLead = nomeLead;
        this.vendedorId = vendedorId;
        this.nomeVendedor = nomeVendedor;
        this.etapa = etapa;
        this.ultimaAtividade = ultimaAtividade;
        this.diasParados = diasParados;
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

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getNomeVendedor() {
        return nomeVendedor;
    }

    public void setNomeVendedor(String nomeVendedor) {
        this.nomeVendedor = nomeVendedor;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public LocalDateTime getUltimaAtividade() {
        return ultimaAtividade;
    }

    public void setUltimaAtividade(LocalDateTime ultimaAtividade) {
        this.ultimaAtividade = ultimaAtividade;
    }

    public Integer getDiasParados() {
        return diasParados;
    }

    public void setDiasParados(Integer diasParados) {
        this.diasParados = diasParados;
    }
}
