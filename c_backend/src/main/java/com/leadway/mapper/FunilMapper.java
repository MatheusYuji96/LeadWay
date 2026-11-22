package com.leadway.mapper;

import com.leadway.dto.response.FunilResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.entity.LeadEtapaHistorico;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class FunilMapper {

    private final LeadEtapaHistoricoRepository leadEtapaHistoricoRepository;

    public FunilMapper(LeadEtapaHistoricoRepository leadEtapaHistoricoRepository) {
        this.leadEtapaHistoricoRepository = leadEtapaHistoricoRepository;
    }

    public FunilResponseDTO toFunil(Lead lead, Integer leadId) {
        FunilResponseDTO dto = new FunilResponseDTO();
        if (lead.getEtapa() != null) {
            dto.setCurrentStage(lead.getEtapa().getNome());
        }

        List<LeadEtapaHistorico> historico = leadEtapaHistoricoRepository
                .findByLead_IdOrderByCriadoEmDesc(leadId);
        LocalDateTime entrada = historico.isEmpty() || historico.get(0).getCriadoEm() == null
                ? lead.getCriadoEm()
                : historico.get(0).getCriadoEm();

        dto.setEnteredStageAt(entrada == null ? null : entrada.toLocalDate().toString());
        dto.setDaysInCurrentStage(entrada == null
                ? 0
                : (int) ChronoUnit.DAYS.between(entrada.toLocalDate(), LocalDateTime.now().toLocalDate()));
        return dto;
    }
}
