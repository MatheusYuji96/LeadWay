package com.leadway.service;

import com.leadway.dto.response.CadenciaResponseDTO;
import com.leadway.entity.Cadencia;
import com.leadway.exception.CadenciaNaoRegistradaException;
import com.leadway.mapper.CadenciaMapper;
import com.leadway.repository.CadenciaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CadenciaService {

    private final CadenciaRepository cadenciaRepository;
    private final CadenciaMapper cadenciaMapper;

    public CadenciaService(CadenciaRepository cadenciaRepository, CadenciaMapper cadenciaMapper) {
        this.cadenciaRepository = cadenciaRepository;
        this.cadenciaMapper = cadenciaMapper;
    }

    public CadenciaResponseDTO buscar(Integer id){
        CadenciaResponseDTO responseDTO = cadenciaMapper.toCadencia(id);
        return responseDTO;
    }

}
