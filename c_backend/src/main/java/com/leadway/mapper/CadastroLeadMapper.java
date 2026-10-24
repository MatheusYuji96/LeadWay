package com.leadway.mapper;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.LeadResponseDTO;
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
        lead.setTelefone(cadastroLeadRequestDTO.getTelefone());
        lead.setEndereco(toEndereco(cadastroLeadRequestDTO.getEndereco()));
        lead.setPotencial(cadastroLeadRequestDTO.getPotencial());
        lead.setAtivo(true);
        return lead;
    }

    public LeadResponseDTO toResponse(Lead lead){
        LeadResponseDTO leadResponse = new LeadResponseDTO();
        leadResponse.setCnpj(lead.getCnpj());
        leadResponse.setRazaoSocial(lead.getRazaoSocial());
        leadResponse.setNomeFantasia(lead.getNomeFantasia());
        leadResponse.setSetor(toSetorResponse(lead.getSetor()));
        leadResponse.setEmail(lead.getEmail());
        leadResponse.setTelefone(lead.getTelefone());
        leadResponse.setEtapa(toEtapaResponse(lead.getEtapa()));
        leadResponse.setEndereco(toEnderecoResponse(lead.getEndereco()));
        leadResponse.setVendedor(toVendedorResponse(lead.getVendedor()));
        leadResponse.setPotencial(lead.getPotencial());
        leadResponse.setAtivo(lead.getAtivo());
        return leadResponse;
    }

    public LeadResponseDTO.VendedorDTO toVendedorResponse(Vendedor vendedor){
        LeadResponseDTO.VendedorDTO dto = new LeadResponseDTO.VendedorDTO();
        dto.setCodVendedor(vendedor.getCodVendedor());
        dto.setNome(vendedor.getNome());
        dto.setEmail(vendedor.getEmail());
        dto.setTelefone(vendedor.getTelefone());
        dto.setAtivo(vendedor.getAtivo());
        return dto;
    }

    public LeadResponseDTO.EtapaDTO toEtapaResponse (Etapa etapa){
        LeadResponseDTO.EtapaDTO dto = new LeadResponseDTO.EtapaDTO();
        dto.setNome(etapa.getNome());
        dto.setOrdem(etapa.getOrdem());
        return dto;
    }
    public LeadResponseDTO.SetorDTO toSetorResponse(Setor setor){
        LeadResponseDTO.SetorDTO setorDTO = new LeadResponseDTO.SetorDTO();
        setorDTO.setNome(setor.getNome());
        return setorDTO;
    }

    public LeadResponseDTO.EnderecoDTO toEnderecoResponse(Endereco endereco){
        LeadResponseDTO.EnderecoDTO enderecoDTO = new LeadResponseDTO.EnderecoDTO();
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
