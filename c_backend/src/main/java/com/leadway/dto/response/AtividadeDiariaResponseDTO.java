package com.leadway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados retornados sobre as atividades diárias planejadas")
public class AtividadeDiariaResponseDTO {

    @Schema(
            description = "Dia em que as atividades foram agendadas",
            example = "2026-10-20"
    )
    private LocalDate dia;

    @Schema(
            description = "Tarefas classificadas como contato",
            example = "4"
    )
    private Integer contatos;

    @Schema(
            description = "Tarefas classificadas como portfólios",
            example = "6"
    )
    private Integer portfolios;

    @Schema(
            description = "Tarefas classificadas como follow-ups",
            example = "8"
    )
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
