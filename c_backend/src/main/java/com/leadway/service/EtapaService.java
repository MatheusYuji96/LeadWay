package com.leadway.service;

import com.leadway.entity.Etapa;
import com.leadway.repository.EtapaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtapaService {

    private final EtapaRepository etapaRepository;

    public EtapaService(EtapaRepository etapaRepository) {
        this.etapaRepository = etapaRepository;
    }

    public List<Etapa> buscarTodos(){
        List<Etapa> lista = etapaRepository.findAll();
        return lista;
    }
}
