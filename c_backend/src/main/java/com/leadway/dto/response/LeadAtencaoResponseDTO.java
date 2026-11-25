package com.leadway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados retornados sobre um Lead em estado de atenção")
public class LeadAtencaoResponseDTO {

    @Schema(
            description = "Identificador do Lead em estado de atenção",
            example = "1"
    )
    private Integer leadId;

    @Schema(
            description = "Nome do lead em estado de atenção",
            example = "Empresa Exemplo INC"
    )
    private String nomeLead;

    @Schema(
            description = "Etapa atual no fúnil de vendas",
            example = "Follow-up"
    )
    private String etapa;

    private String prioridade;

    private String cadencia;

    @Schema(
            description = "Data e hora de último contato com o cliente",
            example = "2026-10-16T09:30:00"
    )
    private LocalDateTime ultimoContato;


    public LeadAtencaoResponseDTO(Integer leadId,
                                  String nomeLead,
                                  String etapa,
                                  String prioridade,
                                  String cadencia,
                                  LocalDateTime ultimoContato) {
        this.leadId = leadId;
        this.nomeLead = nomeLead;
        this.etapa = etapa;
        this.prioridade = prioridade;
        this.cadencia = cadencia;
        this.ultimoContato = ultimoContato;
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

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getCadencia() {
        return cadencia;
    }

    public void setCadencia(String cadencia) {
        this.cadencia = cadencia;
    }

    public LocalDateTime getUltimoContato() {
        return ultimoContato;
    }

    public void setUltimoContato(LocalDateTime ultimoContato) {
        this.ultimoContato = ultimoContato;
    }
}
