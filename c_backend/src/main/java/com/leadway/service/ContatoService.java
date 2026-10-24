package com.leadway.service;

import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import com.leadway.exception.EmailJaCadastradoException;
import com.leadway.exception.LeadNaoEncontradoException;
import com.leadway.mapper.ContatoMapper;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.ContatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final LeadRepository leadRepository;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoService(ContatoRepository contatoRepository, LeadRepository leadRepository) {
        this.contatoRepository = contatoRepository;
        this.leadRepository = leadRepository;
    }

    public List<Contato> listarPorFiltro(String termo, Integer leadId){
        List<Contato> lista = contatoRepository.findByFiltros(termo, leadId);
        return lista;
    }

    public Contato salvar(ContatoRequestDTO dto){
        Contato contato = contatoMapper.toEntity(dto);
        if (contatoRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        Optional<Lead> leadOpt = leadRepository.findById(dto.getLeadId());
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead informado não existe");
        }
        Lead lead = leadOpt.get();
        contato.setLead(lead);
        contatoRepository.save(contato);
        return contato;
    }
}
