package com.leadway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados sobre os indicadores da dashboard")
public class DashboardIndicadoresResponseDTO {

    @Schema(
            description = "Total de leads em responsabilidade da equipe",
            example = "11"
    )
    private Integer totaLeads;

    @Schema(
            description = "Total de compromissos ao longo da semana",
            example = "18"
    )
    private Integer compromissosSemana;

    @Schema(
            description = "Total de leads na etapa de prospecção",
            example = "6"
    )
    private Integer leadsEmProspeccao;

    @Schema(
            description = "Total de leads sem atualização, contato ou acompanhamento",
            example = "3"
    )
    private Integer leadsSemAtualizacao;

    public DashboardIndicadoresResponseDTO(Integer totaLeads, Integer compromissosSemana, Integer leadsEmProspeccao, Integer leadsSemAtualizacao) {
        this.totaLeads = totaLeads;
        this.compromissosSemana = compromissosSemana;
        this.leadsEmProspeccao = leadsEmProspeccao;
        this.leadsSemAtualizacao = leadsSemAtualizacao;
    }

    public Integer getTotaLeads() {
        return totaLeads;
    }

    public void setTotaLeads(Integer totaLeads) {
        this.totaLeads = totaLeads;
    }

    public Integer getCompromissosSemana() {
        return compromissosSemana;
    }

    public void setCompromissosSemana(Integer compromissosSemana) {
        this.compromissosSemana = compromissosSemana;
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
