package com.leadway.dto.response;

import java.time.LocalDateTime;

public class ProximosCompromissosResponseDTO {

    private LocalDateTime horario;
    private String nomeLead;
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
