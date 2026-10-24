package com.leadway.dto.response;

import java.time.LocalDate;

public class AtividadeDiariaResponseDTO {

    private LocalDate dia;
    private Integer contatos;
    private Integer portfolios;
    private Integer followUps;

    public AtividadeDiariaResponseDTO(LocalDate dia, Integer contatos, Integer portfolios, Integer followUps) {
        this.dia = dia;
        this.contatos = contatos;
        this.portfolios = portfolios;
        this.followUps = followUps;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public Integer getContatos() {
        return contatos;
    }

    public void setContatos(Integer contatos) {
        this.contatos = contatos;
    }

    public Integer getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(Integer portfolios) {
        this.portfolios = portfolios;
    }

    public Integer getFollowUps() {
        return followUps;
    }

    public void setFollowUps(Integer followUps) {
        this.followUps = followUps;
    }
}
