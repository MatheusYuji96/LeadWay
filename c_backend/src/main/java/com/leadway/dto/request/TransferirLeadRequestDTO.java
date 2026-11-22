package com.leadway.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferirLeadRequestDTO {

    @NotNull
    @JsonAlias({ "representativeId", "responsavelId" })
    private Integer vendedorId;

    @Size(max = 100)
    @JsonAlias("reason")
    private String motivacao;

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getMotivacao() {
        return motivacao;
    }

    public void setMotivacao(String motivacao) {
        this.motivacao = motivacao;
    }
}
