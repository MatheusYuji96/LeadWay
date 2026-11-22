package com.leadway.mapper;

import com.leadway.dto.response.LeadDetalhadoResponseDTO;
import com.leadway.dto.response.TimelineResponseDTO;
import com.leadway.entity.Interacao;
import com.leadway.entity.Lead;

import java.time.LocalDateTime;

public class LeadDetalheMapper {

    public TimelineResponseDTO toTimeline(Interacao interacao) {
        TimelineResponseDTO dto = new TimelineResponseDTO();
        dto.setId(interacao.getId());
        dto.setTipo(interacao.getTipo());
        dto.setDescricao(interacao.getDescricao());
        LocalDateTime dataHora = interacao.getDataHora() != null
                ? interacao.getDataHora()
                : interacao.getCriadoEm();
        dto.setDataHora(dataHora == null ? null : dataHora.toString());
        return dto;
    }
}
