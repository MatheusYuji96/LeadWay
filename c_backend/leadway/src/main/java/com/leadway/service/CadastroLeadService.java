package com.leadway.service;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.entity.Lead;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.repository.CadastroLeadRepository;

public class CadastroLeadService {

    private final CadastroLeadRepository cadastroLeadRepository;

    public CadastroLeadService(CadastroLeadRepository cadastroLeadRepository) {
        this.cadastroLeadRepository = cadastroLeadRepository;
    }

    public Lead salvar(Lead leadCadastrar){
        Lead cadastro = cadastroLeadRepository.save(leadCadastrar);
        return cadastro;
    }
}
