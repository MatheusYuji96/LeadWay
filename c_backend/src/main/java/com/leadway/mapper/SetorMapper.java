package com.leadway.mapper;

import com.leadway.dto.response.SetorResponseDTO;
import com.leadway.entity.Setor;
import org.springframework.stereotype.Component;

@Component
public class SetorMapper {

    public SetorResponseDTO toResponse(Setor setor){
        SetorResponseDTO dto = new SetorResponseDTO();
        dto.setId(setor.getId());
        dto.setNome(setor.getNome());
        return dto;
    }
}
