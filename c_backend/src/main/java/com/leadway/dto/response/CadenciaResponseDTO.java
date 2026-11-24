package com.leadway.dto.response;

/**
 * Cadência de acompanhamento de um lead.
 * Campos de apresentação (rótulos, progresso, plano) ficam no front.
 */
public class CadenciaResponseDTO {

    private Integer id;
    private String status;
    private String frequencia;
    private Integer intervaloDias;
    private String dataInicio;
    private String dataFim;
    private String ultimoContatoEm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public Integer getIntervaloDias() {
        return intervaloDias;
    }

    public void setIntervaloDias(Integer intervaloDias) {
        this.intervaloDias = intervaloDias;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getUltimoContatoEm() {
        return ultimoContatoEm;
    }

    public void setUltimoContatoEm(String ultimoContatoEm) {
        this.ultimoContatoEm = ultimoContatoEm;
    }
}
