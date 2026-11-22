package com.leadway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CadastroInteracaoRequestDTO {

    @NotBlank
    @Size(max = 45)
    private String tipo;

    private LocalDateTime dataHora;

    @NotBlank
    private String descricao;

    private Integer etapaId;

    public @NotBlank @Size(max = 45) String getTipo() {
        return tipo;
    }

    public void setTipo(@NotBlank @Size(max = 45) String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Integer etapaId) {
        this.etapaId = etapaId;
    }
}
