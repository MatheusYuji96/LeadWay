package com.leadway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CadastroInteracaoRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @NotBlank
    @Size(max = 45)
    private String tipo;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank
    private String descricao;

    @NotNull
    private Integer etapaId;

    public @NotBlank @Size(max = 100) String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NotBlank @Size(max = 100) String titulo) {
        this.titulo = titulo;
    }

    public @NotBlank @Size(max = 45) String getTipo() {
        return tipo;
    }

    public void setTipo(@NotBlank @Size(max = 45) String tipo) {
        this.tipo = tipo;
    }

    public @NotNull LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(@NotNull LocalDateTime dataHora) {
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

