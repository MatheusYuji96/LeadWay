package com.leadway.leadway.dto.request;

import com.leadway.leadway.entity.Endereco;
import com.leadway.leadway.entity.Etapa;
import com.leadway.leadway.entity.Setor;
import com.leadway.leadway.entity.Vendedor;

public class CadastroLeadRequestDTO {

    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private SetorDto setor;
    private String email;
    private EtapaDto etapa;
    private EnderecoDto endereco;
    private String telefone;
    private String potencial;
    private VendedorDto vendedor;

    public static class VendedorDto{
        private String codVendedor;
        private String nome;
        private String email;
        private String senha;
        private String telefone;
        private Boolean ativo;

        public String getCodVendedor() {
            return codVendedor;
        }

        public void setCodVendedor(String codVendedor) {
            this.codVendedor = codVendedor;
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

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }

        public String getTelefone() {
            return telefone;
        }

        public void setTelefone(String telefone) {
            this.telefone = telefone;
        }

        public Boolean getAtivo() {
            return ativo;
        }

        public void setAtivo(Boolean ativo) {
            this.ativo = ativo;
        }
    }

    public static class EtapaDto{
        private String nome;
        private Integer ordem;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Integer getOrdem() {
            return ordem;
        }

        public void setOrdem(Integer ordem) {
            this.ordem = ordem;
        }
    }

    public static class SetorDto{
        private String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    public static class EnderecoDto {
        private String rua;
        private String cep;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
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

    public SetorDto getSetor() {
        return setor;
    }

    public void setSetor(SetorDto setor) {
        this.setor = setor;
    }

    public EtapaDto getEtapa() {
        return etapa;
    }

    public void setEtapa(EtapaDto etapa) {
        this.etapa = etapa;
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

    public VendedorDto getVendedor() {
        return vendedor;
    }

    public void setVendedor(VendedorDto vendedor) {
        this.vendedor = vendedor;
    }
}
