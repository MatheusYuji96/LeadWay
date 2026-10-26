package com.leadway.dto.request;

import com.leadway.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados necessários para atualizar um Lead no sistema")
public class AtualizarLeadRequestDTO {

    @Schema(
            description = "Dados consolidados de endereço da empresa"
    )
    private EnderecoDto endereco;

    @Schema(
            description = "Identificador do vendedor responsável por acompanhar o lead",
            example = "2"
    )
    private Integer vendedorId;

    @Schema(
            description = "Identificador da etapa atual do funil de vendas da empresa",
            example = "2"
    )
    private Integer etapaId;

    @Schema(
            description = "E-mail corporativo da empresa",
            example = "empresaexemplo@outlook.com"
    )
    @Email
    private String email;

    @Schema(
            description = "Telefone principal da empresa",
            example = "11992220000"
    )
    private String telefone;

    @Schema(
            description = "Potencial do Lead definido pelo usuário",
            example = "BAIXO"
    )
    private String potencial;

    @Schema(
            description = "Data de primeiro contato com o cliente",
            example = "2026-10-20T09:30:00"
    )
    private LocalDateTime dataPrimeiroContato;

    @Schema(
            description = "Data de primeira visita com o cliente",
            example = "2026-10-25T14:00:00"
    )
    private LocalDateTime dataPrimeiraVisita;

    @Schema(
            description = "Motivo de perda de oportunidade com o cliente",
            example = "Falta de orçamento"
    )
    private String motivoPerda;

    @Schema(
            description = "Status de atividade do Lead",
            example = "0"
    )
    private Boolean ativo;

    public static class EnderecoDto {

        @Schema(
                description = "Rua do endereço",
                example = "Avenida Cardeal da Silva"
        )
        private String rua;

        @Schema(
                description = "CEP do endereço",
                example = "40225610"
        )
        private String cep;

        @Schema(
                description = "Número do imóvel",
                example = "50"
        )
        private String numero;

        @Schema(
                description = "Complemento do endereço",
                example = "Sala 2"
        )
        private String complemento;

        @Schema(
                description = "Bairro do endereço",
                example = "Rio Vermelho"
        )
        private String bairro;

        @Schema(
                description = "Cidade do endereço",
                example = "Salvador"
        )
        private String cidade;

        @Schema(
                description = "Estado do endereço",
                example = "Bahia"
        )
        private String estado;


        public String getRua() {
            return rua;
        }

        public void setRua(String rua) {
            this.rua = rua;
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }

    public EnderecoDto getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDto endereco) {
        this.endereco = endereco;
    }

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Integer getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Integer etapaId) {
        this.etapaId = etapaId;
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

    public String getPotencial() {
        return potencial;
    }

    public void setPotencial(String potencial) {
        this.potencial = potencial;
    }

    public LocalDateTime getDataPrimeiroContato() {
        return dataPrimeiroContato;
    }

    public void setDataPrimeiroContato(LocalDateTime dataPrimeiroContato) {
        this.dataPrimeiroContato = dataPrimeiroContato;
    }

    public LocalDateTime getDataPrimeiraVisita() {
        return dataPrimeiraVisita;
    }

    public void setDataPrimeiraVisita(LocalDateTime dataPrimeiraVisita) {
        this.dataPrimeiraVisita = dataPrimeiraVisita;
    }

    public String getMotivoPerda() {
        return motivoPerda;
    }

    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
