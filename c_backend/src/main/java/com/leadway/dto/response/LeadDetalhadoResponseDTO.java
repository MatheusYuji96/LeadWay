package com.leadway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Envelope devolvido por GET /leads/{id}.
 * Os campos Java permanecem em português, mas o JSON usa o contrato já esperado
 * pela tela de detalhe: lead, contacts, timeline, owner, funnel e cadence.
 */
public class LeadDetalhadoResponseDTO {

    private LeadResponseDTO lead;

    @JsonProperty("contacts")
    private List<ContatoResponseDTO> contatos;

    @JsonProperty("timeline")
    private List<TimelineResponseDTO> interacoes;

    @JsonProperty("owner")
    private ResponsavelResponseDTO responsavel;

    @JsonProperty("funnel")
    private FunilResponseDTO funil;

    @JsonProperty("cadence")
    private CadenciaResponseDTO cadencia;

    public LeadResponseDTO getLead() {
        return lead;
    }

    public void setLead(LeadResponseDTO lead) {
        this.lead = lead;
    }

    public List<ContatoResponseDTO> getContatos() {
        return contatos;
    }

    public void setContatos(List<ContatoResponseDTO> contatos) {
        this.contatos = contatos;
    }

    public List<TimelineResponseDTO> getInteracoes() {
        return interacoes;
    }

    public void setInteracoes(List<TimelineResponseDTO> interacoes) {
        this.interacoes = interacoes;
    }

    public ResponsavelResponseDTO getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(ResponsavelResponseDTO responsavel) {
        this.responsavel = responsavel;
    }

    public FunilResponseDTO getFunil() {
        return funil;
    }

    public void setFunil(FunilResponseDTO funil) {
        this.funil = funil;
    }

    public CadenciaResponseDTO getCadencia() {
        return cadencia;
    }

    public void setCadencia(CadenciaResponseDTO cadencia) {
        this.cadencia = cadencia;
    }
}
