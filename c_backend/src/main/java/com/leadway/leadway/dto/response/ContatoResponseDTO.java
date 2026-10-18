package com.leadway.leadway.dto.response;

import com.leadway.leadway.entity.ClassificacaoContato;
import com.leadway.leadway.entity.Lead;

public class ContatoResponseDTO {

    private String nome;
    private String cargo;
    private String email;
    private String whatsapp;
    private Lead lead;
    private ClassificacaoContato contato;
    private Boolean ativo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
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
