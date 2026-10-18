package com.leadway.leadway.service;

import com.leadway.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.leadway.entity.Lead;
import com.leadway.leadway.mapper.CadastroLeadMapper;
import com.leadway.leadway.repository.CadastroLeadRepository;

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
