package com.leadway.dto.request;

import com.leadway.entity.ClassificacaoContato;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados necessários para atualizar um contato no sistema")
public class AtualizarContatoRequestDTO {

    @Schema(
            description = "Cargo do contato na empresa",
            example = "Gerente Comercial"
    )
    private String cargo;

    @Schema(
            description = "E-mail do contato do Lead",
            example = "marciosantosnascimento123@outlook.com"
    )
    @Email
    private String email;

    @Schema(
            description = "Número de WhatsApp do contato",
            example = "1166966766"
    )
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]+$")
    private String whatsapp;

    @Schema(
            description = "Enumerador responsável pela classificação devida do contato",
            example = "DECISOR"
    )
    private ClassificacaoContato contato;

    @Schema(
            description = "Status de atividade do contato",
            example = "0"
    )
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
