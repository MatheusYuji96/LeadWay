package com.leadway.service;

import com.leadway.entity.Setor;
import com.leadway.repository.SetorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetorService {

    private final SetorRepository setorRepository;

    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public List<Setor> buscar(){
        List<Setor> lista = setorRepository.findAll();
        return lista;
    }
}
