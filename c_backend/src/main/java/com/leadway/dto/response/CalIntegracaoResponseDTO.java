package com.leadway.dto.response;

import java.time.LocalDateTime;

public class CalIntegracaoResponseDTO {

    private boolean conectado;
    private String username;
    private LocalDateTime conectadoEm;

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getConectadoEm() {
        return conectadoEm;
    }

    public void setConectadoEm(LocalDateTime conectadoEm) {
        this.conectadoEm = conectadoEm;
    }
}
