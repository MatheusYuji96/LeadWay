package com.leadway.dto.request;

import com.leadway.entity.ClassificacaoContato;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados necessários para cadastrar o contato de um Lead no sistema")
public class ContatoRequestDTO {

    @Schema(
            description = "Nome do contato do Lead",
            example = "Márcio Santos Nascimento"
    )
    @NotBlank
    private String nome;

    @Schema(
            description = "Cargo do contato na empresa",
            example = "Consultor de Vendas"
    )
    @NotBlank
    private String cargo;

    @Schema(
            description = "E-mail do contato do Lead",
            example = "marciosantosnascimento@email.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Número de WhatsApp do contato",
            example = "1188988788"
    )
    @NotBlank
    private String whatsapp;

    @Schema(
            description = "Identificador estrangeiro do Lead associado ao contato",
            example = "1"
    )
    private Integer leadId;

    @Schema(
            description = "Enumerador responsável pela classificação devida do contato",
            example = "PADRAO"
    )
    @NotNull
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

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
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
