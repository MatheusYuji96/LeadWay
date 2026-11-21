package com.leadway.mapper;

import com.leadway.dto.response.EtapaResponseDTO;
import com.leadway.entity.Etapa;

public class EtapaMapper {

    public EtapaResponseDTO toResponse(Etapa etapa){
        EtapaResponseDTO dto = new EtapaResponseDTO();
        dto.setId(etapa.getId());
        dto.setNome(etapa.getNome());
        dto.setOrdem(etapa.getOrdem());
        return dto;
    }
}
