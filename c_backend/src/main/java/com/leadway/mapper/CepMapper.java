package com.leadway.mapper;

import com.leadway.dto.integration.ViaCepResponseDTO;
import com.leadway.dto.response.CepResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CepMapper {

    public CepResponseDTO toResponse(ViaCepResponseDTO dto, String cepNormal){
        CepResponseDTO responseDTO = new CepResponseDTO();
        responseDTO.setCep(cepNormal);
        responseDTO.setRua(nullToEmpty(dto.getLogradouro()));
        responseDTO.setComplemento(nullToEmpty(dto.getComplemento()));
        responseDTO.setBairro(nullToEmpty(dto.getBairro()));
        responseDTO.setCidade(nullToEmpty(dto.getLocalidade()));
        responseDTO.setUf(nullToEmpty(dto.getUf()));
        responseDTO.setEstado(!isBlank(dto.getEstado()) ? dto.getEstado() : nullToEmpty(dto.getUf()));
        responseDTO.setPais("Brasil");
        return responseDTO;
    }

    private String nullToEmpty(String v) { return v == null ? "" : v.trim(); }
    private boolean isBlank(String v) { return v == null || v.trim().isEmpty(); }
}
