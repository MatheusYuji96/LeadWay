package com.leadway.dto.request;

public class AgendamentoRequestDTO {

    private Integer vendedorId;
    private Integer leadId;
    private Integer contatoId;
    private String slotStart;
    private String tipo;
    private String notas;

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public Integer getContatoId() {
        return contatoId;
    }

    public void setContatoId(Integer contatoId) {
        this.contatoId = contatoId;
    }

    public String getSlotStart() {
        return slotStart;
    }

    public void setSlotStart(String slotStart) {
        this.slotStart = slotStart;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
