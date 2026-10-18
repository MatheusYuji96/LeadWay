package com.leadway.leadway.service;

import com.leadway.leadway.dto.request.ContatoRequestDTO;
import com.leadway.leadway.entity.Contato;
import com.leadway.leadway.entity.Lead;
import com.leadway.leadway.exception.EmailJaCadastradoException;
import com.leadway.leadway.exception.LeadNaoEncontradoException;
import com.leadway.leadway.mapper.ContatoMapper;
import com.leadway.leadway.repository.CadastroLeadRepository;
import com.leadway.leadway.repository.ContatoRepository;

import java.util.Optional;

public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final CadastroLeadRepository cadastroLeadRepository;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoService(ContatoRepository contatoRepository, CadastroLeadRepository cadastroLeadRepository) {
        this.contatoRepository = contatoRepository;
        this.cadastroLeadRepository = cadastroLeadRepository;
    }

    public Contato salvar(ContatoRequestDTO dto){
        Contato contato = contatoMapper.toEntity(dto);
        if (contatoRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        Optional<Lead> leadOpt = cadastroLeadRepository.findById(dto.getLeadId());
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead informado não existe");
        }
        Lead lead = leadOpt.get();
        contato.setLead(lead);
        return contato;
    }
}
