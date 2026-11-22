package com.leadway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Responsável pelo lead no cartão de detalhe.
 * Os nomes JSON seguem o contrato já consumido pelo front (owner).
 */
public class ResponsavelResponseDTO {

    private Integer id;

    @JsonProperty("name")
    private String nome;

    private String email;

    @JsonProperty("phone")
    private String telefone;

    @JsonProperty("initials")
    private String iniciais;

    @JsonProperty("since")
    private String desde;

    private Integer leads;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getIniciais() {
        return iniciais;
    }

    public void setIniciais(String iniciais) {
        this.iniciais = iniciais;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public Integer getLeads() {
        return leads;
    }

    public void setLeads(Integer leads) {
        this.leads = leads;
    }
}
