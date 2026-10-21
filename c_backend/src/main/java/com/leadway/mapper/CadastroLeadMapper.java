package com.leadway.mapper;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.CadastroLeadResponseDTO;
import com.leadway.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CadastroLeadMapper {

    public Lead toEntity(CadastroLeadRequestDTO cadastroLeadRequestDTO){
        Lead lead = new Lead();
        lead.setCnpj(cadastroLeadRequestDTO.getCnpj());
        lead.setRazaoSocial(cadastroLeadRequestDTO.getRazaoSocial());
        lead.setNomeFantasia(cadastroLeadRequestDTO.getNomeFantasia());
        lead.setEmail(cadastroLeadRequestDTO.getEmail());
        lead.setEndereco(toEndereco(cadastroLeadRequestDTO.getEndereco()));
        lead.setPotencial(cadastroLeadRequestDTO.getPotencial());
        lead.setAtivo(true);
        return lead;
    }

    public CadastroLeadResponseDTO toResponse(Lead lead){
        CadastroLeadResponseDTO leadResponse = new CadastroLeadResponseDTO();
        leadResponse.setCnpj(lead.getCnpj());
        leadResponse.setRazaoSocial(lead.getRazaoSocial());
        leadResponse.setNomeFantasia(lead.getNomeFantasia());
        leadResponse.setSetor(toSetorResponse(lead.getSetor()));
        leadResponse.setEmail(lead.getEmail());
        leadResponse.setEtapa(toEtapaResponse(lead.getEtapa()));
        leadResponse.setEndereco(toEnderecoResponse(lead.getEndereco()));
        leadResponse.setVendedor(toVendedorResponse(lead.getVendedor()));
        leadResponse.setPotencial(lead.getPotencial());
        leadResponse.setAtivo(lead.getAtivo());
        return leadResponse;
    }

    public CadastroLeadResponseDTO.VendedorDTO toVendedorResponse(Vendedor vendedor){
        CadastroLeadResponseDTO.VendedorDTO dto = new CadastroLeadResponseDTO.VendedorDTO();
        dto.setCodVendedor(vendedor.getCodVendedor());
        dto.setNome(vendedor.getNome());
        dto.setEmail(vendedor.getEmail());
        dto.setTelefone(vendedor.getTelefone());
        dto.setAtivo(vendedor.getAtivo());
        return dto;
    }

    public CadastroLeadResponseDTO.EtapaDTO toEtapaResponse (Etapa etapa){
        CadastroLeadResponseDTO.EtapaDTO dto = new CadastroLeadResponseDTO.EtapaDTO();
        dto.setNome(etapa.getNome());
        dto.setOrdem(etapa.getOrdem());
        return dto;
    }
    public CadastroLeadResponseDTO.SetorDTO toSetorResponse(Setor setor){
        CadastroLeadResponseDTO.SetorDTO setorDTO = new CadastroLeadResponseDTO.SetorDTO();
        setorDTO.setNome(setor.getNome());
        return setorDTO;
    }

    public CadastroLeadResponseDTO.EnderecoDTO toEnderecoResponse(Endereco endereco){
        CadastroLeadResponseDTO.EnderecoDTO enderecoDTO = new CadastroLeadResponseDTO.EnderecoDTO();
        enderecoDTO.setRua(endereco.getRua());
        enderecoDTO.setNumero(endereco.getNumero());
        enderecoDTO.setCep(endereco.getCep());
        enderecoDTO.setComplemento(endereco.getComplemento());
        enderecoDTO.setBairro(endereco.getBairro());
        enderecoDTO.setCidade(endereco.getCidade());
        enderecoDTO.setEstado(endereco.getEstado());
        enderecoDTO.setPais(endereco.getPais());
        return enderecoDTO;
    }

    public Endereco toEndereco(CadastroLeadRequestDTO.EnderecoDto dto){
        Endereco endereco = new Endereco();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setPais(dto.getPais());
        return endereco;
    }
}
