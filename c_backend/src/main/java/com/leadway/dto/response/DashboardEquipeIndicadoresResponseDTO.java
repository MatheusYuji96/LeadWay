package com.leadway.dto.response;

public class DashboardEquipeIndicadoresResponseDTO {

    private Integer representatesAtivos;
    private Integer leadsCarteira;
    private Integer contatosRealizados;
    private Integer pendenciasEquipe;

    public DashboardEquipeIndicadoresResponseDTO() {
    }

    public DashboardEquipeIndicadoresResponseDTO(Integer representatesAtivos, Integer leadsCarteira, Integer contatosRealizados, Integer pendenciasEquipe) {
        this.representatesAtivos = representatesAtivos;
        this.leadsCarteira = leadsCarteira;
        this.contatosRealizados = contatosRealizados;
        this.pendenciasEquipe = pendenciasEquipe;
    }

    public Integer getRepresentatesAtivos() {
        return representatesAtivos;
    }

    public void setRepresentatesAtivos(Integer representatesAtivos) {
        this.representatesAtivos = representatesAtivos;
    }

    public Integer getLeadsCarteira() {
        return leadsCarteira;
    }

    public void setLeadsCarteira(Integer leadsCarteira) {
        this.leadsCarteira = leadsCarteira;
    }

    public Integer getContatosRealizados() {
        return contatosRealizados;
    }

    public void setContatosRealizados(Integer contatosRealizados) {
        this.contatosRealizados = contatosRealizados;
    }

    public Integer getPendenciasEquipe() {
        return pendenciasEquipe;
    }

    public void setPendenciasEquipe(Integer pendenciasEquipe) {
        this.pendenciasEquipe = pendenciasEquipe;
    }
}
