package com.leadway.dto.response;

public class DashboardIndicadoresResponseDTO {

    private Integer totaLeads;
    private Integer compromissiosSemana;
    private Integer leadsEmProspeccao;
    private Integer leadsSemAtualizacao;

    public DashboardIndicadoresResponseDTO(Integer totaLeads, Integer compromissiosSemana, Integer leadsEmProspeccao, Integer leadsSemAtualizacao) {
        this.totaLeads = totaLeads;
        this.compromissiosSemana = compromissiosSemana;
        this.leadsEmProspeccao = leadsEmProspeccao;
        this.leadsSemAtualizacao = leadsSemAtualizacao;
    }

    public Integer getTotaLeads() {
        return totaLeads;
    }

    public void setTotaLeads(Integer totaLeads) {
        this.totaLeads = totaLeads;
    }

    public Integer getCompromissiosSemana() {
        return compromissiosSemana;
    }

    public void setCompromissiosSemana(Integer compromissiosSemana) {
        this.compromissiosSemana = compromissiosSemana;
    }

    public Integer getLeadsEmProspeccao() {
        return leadsEmProspeccao;
    }

    public void setLeadsEmProspeccao(Integer leadsEmProspeccao) {
        this.leadsEmProspeccao = leadsEmProspeccao;
    }

    public Integer getLeadsSemAtualizacao() {
        return leadsSemAtualizacao;
    }

    public void setLeadsSemAtualizacao(Integer leadsSemAtualizacao) {
        this.leadsSemAtualizacao = leadsSemAtualizacao;
    }
}
