package com.leadway.mapper;

import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.entity.Interacao;

public class InteracaoMapper {

    public static InteracaoResponseDTO toResponse(Interacao interacao) {
        InteracaoResponseDTO dto = new InteracaoResponseDTO();
        dto.setId(interacao.getId());
        dto.setTipo(interacao.getTipo());
        dto.setDataHora(interacao.getDataHora());
        dto.setDescricao(interacao.getDescricao());

        return dto;
    }
}
