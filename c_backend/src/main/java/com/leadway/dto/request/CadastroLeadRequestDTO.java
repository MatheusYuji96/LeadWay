package com.leadway.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados necessários para cadastrar um Lead no sistema")
public class CadastroLeadRequestDTO {

    @Schema(
            description = "CNPJ da empresa",
            example = "12345678000199"
    )
    @NotBlank
    private String cnpj;

    @Schema(
            description = "Razão social da empresa",
            example = "Empresa Exemplo LTDA"
    )
    @NotBlank
    private String razaoSocial;

    @Schema(
            description = "Nome fantasia da empresa",
            example = "Empresa Exemplo"
    )
    @NotBlank
    private String nomeFantasia;

    @Schema(
            description = "Identificador do setor da empresa",
            example = "2"
    )
    private Integer setorId;

    @Schema(
            description = "E-mail corporativo da empresa",
            example = "email@empresaexemplo.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Identificador da etapa atual do funil de vendas da empresa",
            example = "1"
    )
    private Integer etapaId;

    @Schema(
            description = "Dados consolidados de endereço da empresa"
    )
    @NotNull
    @Valid
    private EnderecoDto endereco;

    @Schema(
            description = "Telefone principal da empresa",
            example = "11991110000"
    )
    private String telefone;

    @Schema(
            description = "Potencial do Lead definido pelo usuário",
            example = "ALTO"
    )
    private String potencial;

    @Schema(
            description = "Identificador do vendedor responsável por acompanhar o lead",
            example = "1"
    )
    private Integer vendedorId;

    @Schema(
            description = "Dados de endereço do Lead"
    )
    public static class EnderecoDto {

        @Schema(
                description = "Rua do endereço",
                example = "Rua Oscar Freire"
        )
        @NotBlank
        private String rua;

        @Schema(
                description = "CEP do endereço",
                example = "01310100"
        )
        @NotBlank
        private String cep;

        @Schema(
                description = "Número do imóvel",
                example = "110"
        )
        @NotBlank
        private String numero;

        @Schema(
                description = "Complemento do endereço",
                example = "Sala 4"
        )
        private String complemento;

        @Schema(
                description = "Bairro do endereço",
                example = "Centro"
        )
        @NotBlank
        private String bairro;

        @Schema(
                description = "Cidade do endereço",
                example = "São Paulo"
        )
        @NotBlank
        private String cidade;

        @Schema(
                description = "Estado do endereço",
                example = "São Paulo"
        )
        @NotBlank
        private String estado;

        @Schema(
                description = "País",
                example = "Brasil"
        )
        @NotBlank
        private String pais;

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

        public String getPais() {
            return pais;
        }

        public void setPais(String pais) {
            this.pais = pais;
        }
    }

    public CadastroLeadRequestDTO() {
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public Integer getSetorId() {
        return setorId;
    }

    public void setSetorId(Integer setorId) {
        this.setorId = setorId;
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

    public EnderecoDto getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDto endereco) {
        this.endereco = endereco;
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

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }
}
