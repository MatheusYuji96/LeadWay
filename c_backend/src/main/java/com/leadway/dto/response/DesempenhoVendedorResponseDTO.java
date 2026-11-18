package com.leadway.dto.response;

import com.leadway.entity.Contato;
import com.leadway.entity.Lead;

import java.util.List;

public class DesempenhoVendedorResponseDTO {

    private Integer vendedorId;
    private String nome;
    private String codVendedor;
    private String email;
    private Integer leads;
    private Integer contatos;
    private Integer propostas;
    private Integer pendencias;

    public DesempenhoVendedorResponseDTO() {
    }

    public DesempenhoVendedorResponseDTO(Integer vendedorId, String nome, String codVendedor, String email, Integer leads, Integer contatos, Integer propostas, Integer pendencias) {
        this.vendedorId = vendedorId;
        this.nome = nome;
        this.codVendedor = codVendedor;
        this.email = email;
        this.leads = leads;
        this.contatos = contatos;
        this.propostas = propostas;
        this.pendencias = pendencias;
    }

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodVendedor() {
        return codVendedor;
    }

    public void setCodVendedor(String codVendedor) {
        this.codVendedor = codVendedor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLeads() {
        return leads;
    }

    public void setLeads(Integer leads) {
        this.leads = leads;
    }

    public Integer getContatos() {
        return contatos;
    }

    public void setContatos(Integer contatos) {
        this.contatos = contatos;
    }

    public Integer getPropostas() {
        return propostas;
    }

    public void setPropostas(Integer propostas) {
        this.propostas = propostas;
    }

    public Integer getPendencias() {
        return pendencias;
    }

    public void setPendencias(Integer pendencias) {
        this.pendencias = pendencias;
    }
}
