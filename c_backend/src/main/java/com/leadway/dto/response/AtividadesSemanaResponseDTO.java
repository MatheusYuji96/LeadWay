package com.leadway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Dados retornados sobre as atividades semanais planejadas")
public class AtividadesSemanaResponseDTO {

    @Schema(
            description = "Data que marca o início de uma semana planejada",
            example = "2026-10-26"
    )
    private LocalDate inicioSemana;

    @Schema(
            description = "Data que marca o fim de uma semana planejada",
            example = "2026-10-30"
    )
    private LocalDate fimSemana;

    @Schema(
            description = "Lista de atividades planejadas na semana"
    )
    private List<AtividadeDiariaResponseDTO> atividades;

    public AtividadesSemanaResponseDTO(
            LocalDate inicioSemana,
            LocalDate fimSemana,
            List<AtividadeDiariaResponseDTO> atividades) {
        this.inicioSemana = inicioSemana;
        this.fimSemana = fimSemana;
        this.atividades = atividades;
    }

    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    public void setInicioSemana(LocalDate inicioSemana) {
        this.inicioSemana = inicioSemana;
    }

    public LocalDate getFimSemana() {
        return fimSemana;
    }

    public void setFimSemana(LocalDate fimSemana) {
        this.fimSemana = fimSemana;
    }

    public List<AtividadeDiariaResponseDTO> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<AtividadeDiariaResponseDTO> atividades) {
        this.atividades = atividades;
    }
}
