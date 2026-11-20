package com.leadway.service;

import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.entity.Contato;
import com.leadway.repository.ContatoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContatosService {

    private final ContatoRepository contatoRepository;

    public ContatosService(ContatoRepository contatoRepository) {
        this.contatoRepository = contatoRepository;
    }

    public List<Contato> buscarTodos(){
        List<Contato> lista = contatoRepository.findAll();
        return lista;
    }
}
