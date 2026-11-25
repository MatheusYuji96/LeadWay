package com.leadway.mapper;

import com.leadway.dto.response.CargoResponseDTO;
import com.leadway.entity.Cargo;
import org.springframework.stereotype.Component;

@Component
public class CargoMapper {

    public CargoResponseDTO toResponse(Cargo cargo){
        CargoResponseDTO dto = new CargoResponseDTO();
        dto.setId(cargo.getId());
        dto.setNome(cargo.getNome());
        dto.setDescricao(cargo.getDescricao());
        return dto;
    }
}
