package com.leadway.dto.request;

import com.leadway.entity.ClassificacaoContato;
import jakarta.validation.constraints.*;

public class AtualizarContatoRequestDTO {

    private String cargo;

    @Email
    private String email;

    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]+$")
    private String whatsapp;

    private ClassificacaoContato contato;
    private Boolean ativo;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public ClassificacaoContato getContato() {
        return contato;
    }

    public void setContato(ClassificacaoContato contato) {
        this.contato = contato;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
