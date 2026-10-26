package com.leadway.dto.response;

import java.time.LocalDate;
import java.util.List;

public class AtividadesSemanaResponseDTO {

    private LocalDate inicioSemana;
    private LocalDate fimSemana;
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
