package com.leadway.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Payload de POST /leads/{id}/notes.
 * "description" é o nome enviado pelo modal do front; "descricao" é o nome
 * usado internamente no banco. dataHora, contatoId e etapaId são opcionais.
 */
public class NotaLeadRequestDTO {

    @Size(max = 45)
    private String tipo;

    @NotBlank
    @JsonAlias("description")
    private String descricao;

    private LocalDateTime dataHora;

    private Integer contatoId;

    private Integer etapaId;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getContatoId() {
        return contatoId;
    }

    public void setContatoId(Integer contatoId) {
        this.contatoId = contatoId;
    }

    public Integer getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Integer etapaId) {
        this.etapaId = etapaId;
    }
}
