package com.leadway.dto.response;

/**
 * Item da timeline do detalhe do lead.
 * A tabela interacao não possui coluna de título: o front deriva o rótulo a partir de "tipo".
 */
public class TimelineResponseDTO {

    private Integer id;
    private String tipo;
    private String dataHora;
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
