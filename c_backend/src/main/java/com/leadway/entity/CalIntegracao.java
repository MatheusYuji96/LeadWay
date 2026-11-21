package com.leadway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "cal_integracao")
public class CalIntegracao {

    // A chave primária é o próprio id do usuário: cada usuário tem no máximo uma conexão.
    @Id
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false, length = 100)
    private String calUsername;

    @Column(nullable = false, length = 255)
    private String calApiKey;

    @Column(nullable = false)
    private LocalDateTime conectadoEm;

    public CalIntegracao() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCalUsername() {
        return calUsername;
    }

    public void setCalUsername(String calUsername) {
        this.calUsername = calUsername;
    }

    public String getCalApiKey() {
        return calApiKey;
    }

    public void setCalApiKey(String calApiKey) {
        this.calApiKey = calApiKey;
    }

    public LocalDateTime getConectadoEm() {
        return conectadoEm;
    }

    public void setConectadoEm(LocalDateTime conectadoEm) {
        this.conectadoEm = conectadoEm;
    }
}
