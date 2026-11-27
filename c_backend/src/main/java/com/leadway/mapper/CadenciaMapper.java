package com.leadway.mapper;

import com.leadway.dto.response.CadenciaResponseDTO;
import com.leadway.entity.Cadencia;
import com.leadway.repository.CadenciaRepository;
import com.leadway.repository.InteracaoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CadenciaMapper {

    private final CadenciaRepository cadenciaRepository;
    private final InteracaoRepository interacaoRepository;

    public CadenciaMapper(CadenciaRepository cadenciaRepository, InteracaoRepository interacaoRepository) {
        this.cadenciaRepository = cadenciaRepository;
        this.interacaoRepository = interacaoRepository;
    }

    public CadenciaResponseDTO toCadencia(Integer leadId) {
        List<Cadencia> cadencias = cadenciaRepository.findByLead_IdOrderByCriadoEmDesc(leadId);
        if (cadencias.isEmpty()) {
            return null;
        }
        Cadencia cadencia = cadencias.get(0);

        CadenciaResponseDTO dto = new CadenciaResponseDTO();
        dto.setId(cadencia.getId());
        dto.setStatus(cadencia.getStatus() == null ? null : cadencia.getStatus().name());
        dto.setFrequencia(cadencia.getFrequencia() == null ? null : cadencia.getFrequencia().name());
        dto.setIntervaloDias(cadencia.getIntervaloDias());
        dto.setDataInicio(cadencia.getDataInicio() == null ? null : cadencia.getDataInicio().toString());
        dto.setDataFim(cadencia.getDataFim() == null ? null : cadencia.getDataFim().toString());

        LocalDateTime ultimoContato = interacaoRepository.buscarUltimaAtividadePorLead(leadId);
        dto.setUltimoContatoEm(ultimoContato == null ? null : ultimoContato.toString());
        return dto;
    }
}
