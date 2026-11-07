package com.leadway.dto.response;

import com.leadway.entity.ClassificacaoContato;
import com.leadway.entity.Lead;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após o cadastro de um contato")
public class ContatoResponseDTO {
    private Integer id;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @Schema(
            description = "Nome do contato do Lead",
            example = "Márcio Santos Nascimento"
    )
    private String nome;

    @Schema(
            description = "Cargo do contato na empresa",
            example = "Consultor de Vendas"
    )
    private String cargo;

    @Schema(
            description = "E-mail do contato do Lead",
            example = "marciosantosnascimento@email.com"
    )
    private String email;

    @Schema(
            description = "Número de WhatsApp do contato",
            example = "1188988788"
    )
    private String whatsapp;

    @Schema(
            description = "Dados consolidados do Lead que o contato pertence"
    )
    private Integer leadId;

    @Schema(
            description = "Enumerador responsável pela classificação devida do contato",
            example = "PADRAO"
    )
    private ClassificacaoContato contato;

    @Schema(
            description = "Status de atividade do contato",
            example = "1"
    )
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

    public Integer getLeadId() { return leadId; }
    public void setLeadId(Integer leadId) { this.leadId = leadId; }

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
