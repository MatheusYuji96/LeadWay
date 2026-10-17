package com.leadway.mapper;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.response.CadastroLeadResponseDTO;
import com.leadway.entity.Endereco;
import com.leadway.entity.Lead;
import com.leadway.entity.Setor;

public class CadastroLeadMapper {

    public Lead toEntity(CadastroLeadRequestDTO cadastroLeadRequestDTO){
        Lead lead = new Lead();
        lead.setCnpj(cadastroLeadRequestDTO.getCnpj());
        lead.setRazaoSocial(cadastroLeadRequestDTO.getRazaoSocial());
        lead.setNomeFantasia(cadastroLeadRequestDTO.getNomeFantasia());
        lead.setEmail(cadastroLeadRequestDTO.getEmail());
        Setor setor = new Setor();
        setor.setNome(cadastroLeadRequestDTO.getSetor());
        lead.setSetor(setor);
        Endereco endereco = new Endereco();
        endereco.setRua(cadastroLeadRequestDTO.getRua());
        endereco.setCep(cadastroLeadRequestDTO.getCep());
        endereco.setNumero(cadastroLeadRequestDTO.getNumero());
        endereco.setComplemento(cadastroLeadRequestDTO.getComplemento());
        endereco.setBairro(cadastroLeadRequestDTO.getBairro());
        endereco.setCidade(cadastroLeadRequestDTO.getCidade());
        endereco.setEstado(cadastroLeadRequestDTO.getEstado());
        lead.setEndereco(endereco);
        lead.setPotencial(cadastroLeadRequestDTO.getPotencial());
        lead.setAtivo(cadastroLeadRequestDTO.getAtivo());
        return lead;
    }

    public CadastroLeadResponseDTO toResponse(Lead lead){
        CadastroLeadResponseDTO leadCadastrado = new CadastroLeadResponseDTO();
        lead.setCnpj(leadCadastrado.getCnpj());
        lead.setRazaoSocial(leadCadastrado.getRazaoSocial());
        lead.setNomeFantasia(leadCadastrado.getNomeFantasia());
        lead.setEmail(leadCadastrado.getEmail());
        Setor setor = new Setor();
        setor.setNome(leadCadastrado.getSetor());
        lead.setSetor(setor);
        Endereco endereco = new Endereco();
        endereco.setRua(leadCadastrado.getRua());
        endereco.setCep(leadCadastrado.getCep());
        endereco.setNumero(leadCadastrado.getNumero());
        endereco.setComplemento(leadCadastrado.getComplemento());
        endereco.setBairro(leadCadastrado.getBairro());
        endereco.setCidade(leadCadastrado.getCidade());
        endereco.setEstado(leadCadastrado.getEstado());
        lead.setEndereco(endereco);
        lead.setPotencial(leadCadastrado.getPotencial());
        lead.setAtivo(leadCadastrado.getAtivo());
        return leadCadastrado;
    }
}
