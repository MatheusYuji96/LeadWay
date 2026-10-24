package com.leadway.dto.request;

import com.leadway.entity.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AtualizarLeadRequestDTO {

    private EnderecoDto endereco;
    private Integer vendedorId;
    private Integer etapaId;

    @Email
    private String email;

    private String telefone;
    private String potencial;
    private LocalDateTime dataPrimeiroContato;
    private LocalDateTime dataPrimeiraVisita;
    private String motivoPerda;
    private Boolean ativo;

    public static class EnderecoDto {
        private String rua;
        private String cep;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
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
