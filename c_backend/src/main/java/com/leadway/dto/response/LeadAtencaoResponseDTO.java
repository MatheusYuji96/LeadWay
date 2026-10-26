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

    @Schema(
            description = "Data e hora de último contato com o cliente",
            example = "2026-10-16T09:30:00"
    )
    private LocalDateTime ultimoContato;

    @Schema(
            description = "Próxima ação planejada",
            example = "Agendar visita"
    )
    private String proximaAcao;

    @Schema(
            description = "Data e hora da próxima ação planejada",
            example = "2026-10-30T14:00:00"
    )
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
