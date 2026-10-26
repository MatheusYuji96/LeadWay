package com.leadway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados retornados sobre compromissos perto de ocorrer")
public class ProximosCompromissosResponseDTO {

    @Schema(
            description = "Data e hora do compromisso",
            example = "2026-10-27T10:00:00"
    )
    private LocalDateTime horario;

    @Schema(
            description = "Nome do lead em qual o compromisso envolve",
            example = "Empresa Exemplo INC"
    )
    private String nomeLead;

    @Schema(
            description = "Descrição do compromisso",
            example = "Envolverá contato com..."
    )
    private String descricao;

    public ProximosCompromissosResponseDTO(LocalDateTime horario, String nomeLead, String descricao) {
        this.horario = horario;
        this.nomeLead = nomeLead;
        this.descricao = descricao;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getNomeLead() {
        return nomeLead;
    }

    public void setNomeLead(String nomeLead) {
        this.nomeLead = nomeLead;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
