package com.leadway.dto.response;

import com.leadway.entity.Endereco;
import com.leadway.entity.Setor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após o cadastro de um Lead")
public class CadastroLeadResponseDTO {

    @Schema(
            description = "CNPJ da empresa",
            example = "12345678000199"
    )
    private String cnpj;

    @Schema(
            description = "Razão social da empresa",
            example = "Empresa Exemplo LTDA"
    )
    private String razaoSocial;

    @Schema(
            description = "Nome fantasia da empresa",
            example = "Empresa Exemplo"
    )
    private String nomeFantasia;

    @Schema(
            description = "Dados consolidados do setor da empresa"
    )
    private SetorDTO setor;

    @Schema(
            description = "E-mail principal da empresa",
            example = "empresaexemplo@email.com"
    )
    private String email;

    @Schema(
            description = "Dados consolidados de endereço da empresa"
    )
    private EnderecoDTO endereco;

    @Schema(
            description = "Dados consolidados da etapa do funíl de vendas do lead"
    )
    private EtapaDTO etapa;

    @Schema(
            description = "Dados consolidados do vendedor do lead"
    )
    private VendedorDTO vendedor;

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
            description = "Status de atividade do Lead",
            example = "1"
    )
    private Boolean ativo;

    public CadastroLeadResponseDTO() {
    }

    @Schema(
            description = "Dados do vendedor responsável pelo Lead"
    )
    public static class VendedorDTO{

        @Schema(
                description = "Código de vendedor cadastrado pelo líder de equipe",
                example = "VEN-001"
        )
        private String codVendedor;

        @Schema(
                description = "Nome do usuário vendedor",
                example = "Maria Rocha"
        )
        private String nome;

        @Schema(
                description = "E-mail do usuário vendedor",
                example = "mariarocha@outlook.com"
        )
        private String email;

        @Schema(
                description = "Telefone de contato do usuário vendedor",
                example = "1199899799"
        )
        private String telefone;

        @Schema(
                description = "Status de atividade do usuário vendedor",
                example = "1"
        )
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

    @Schema(
            description = "Dados do setor empresarial do Lead"
    )
    public static class SetorDTO{

        @Schema(
                description = "Nome do setor empresarial do Lead",
                example = "Têxtil"
        )
        private String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    @Schema(
            description = "Dados da etapa do funíl de vendas do Lead"
    )
    public static class EtapaDTO{

        @Schema(
                description = "Nome da etapa do funíl de vendas",
                example = "Qualificação"
        )
        private String nome;

        @Schema(
                description = "Ordem em número da etapa do funíl de vendas",
                example = "1"
        )
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

    @Schema(
            description = "Dados de endereço do Lead"
    )
    public static class EnderecoDTO{

        @Schema(
                description = "Rua do endereço",
                example = "Rua Oscar Freire"
        )
        private String rua;

        @Schema(
                description = "CEP do endereço",
                example = "01310100"
        )
        private String cep;

        @Schema(
                description = "Número do imóvel",
                example = "110"
        )
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
        private String bairro;

        @Schema(
                description = "Cidade do endereço",
                example = "São Paulo"
        )
        private String cidade;

        @Schema(
                description = "Estado do endereço",
                example = "São Paulo"
        )
        private String estado;

        @Schema(
                description = "País",
                example = "Brasil"
        )
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

    public SetorDTO getSetor() {
        return setor;
    }

    public void setSetor(SetorDTO setor) {
        this.setor = setor;
    }

    public EtapaDTO getEtapa() {
        return etapa;
    }

    public void setEtapa(EtapaDTO etapa) {
        this.etapa = etapa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public VendedorDTO getVendedor() {
        return vendedor;
    }

    public void setVendedor(VendedorDTO vendedor) {
        this.vendedor = vendedor;
    }
}
