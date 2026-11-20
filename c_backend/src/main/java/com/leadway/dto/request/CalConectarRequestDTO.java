package com.leadway.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CalConectarRequestDTO {

    // Nome externo mantido como "vendedorId" para não quebrar o front; internamente é usuario_id.
    @JsonAlias("vendedorId")
    private Integer usuarioId;
    private String apiKey;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Alias de compatibilidade com o contrato anterior do front-end.
     */
    @Deprecated
    public Integer getVendedorId() {
        return usuarioId;
    }

    @Deprecated
    public void setVendedorId(Integer vendedorId) {
        this.usuarioId = vendedorId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
