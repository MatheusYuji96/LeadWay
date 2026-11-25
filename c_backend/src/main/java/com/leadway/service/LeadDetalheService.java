package com.leadway.service;

import com.leadway.dto.response.CadenciaResponseDTO;
import com.leadway.dto.response.FunilResponseDTO;
import com.leadway.dto.response.LeadDetalhadoResponseDTO;
import com.leadway.dto.response.ResponsavelResponseDTO;
import com.leadway.dto.response.TimelineResponseDTO;
import com.leadway.entity.Cadencia;
import com.leadway.entity.Interacao;
import com.leadway.entity.Lead;
import com.leadway.entity.LeadEtapaHistorico;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.*;
import com.leadway.repository.CadenciaRepository;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import com.leadway.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Monta o agregado devolvido por GET /leads/{id}.
 * A lista (GET /leads) continua usando apenas LeadResponseDTO.
 */
@Service
public class LeadDetalheService {

    private final LeadService leadService;
    private final CadastroLeadMapper cadastroLeadMapper;
    private final ContatoRepository contatoRepository;
    private final ContatoMapper contatoMapper;
    private final InteracaoRepository interacaoRepository;
    private final LeadDetalheMapper leadDetalheMapper;
    private final ResponsavelMapper responsavelMapper;
    private final FunilMapper funilMapper;
    private final CadenciaMapper cadenciaMapper;

    public LeadDetalheService(LeadService leadService, CadastroLeadMapper cadastroLeadMapper, ContatoRepository contatoRepository, ContatoMapper contatoMapper, InteracaoRepository interacaoRepository, LeadDetalheMapper leadDetalheMapper, ResponsavelMapper responsavelMapper, FunilMapper funilMapper, CadenciaMapper cadenciaMapper) {
        this.leadService = leadService;
        this.cadastroLeadMapper = cadastroLeadMapper;
        this.contatoRepository = contatoRepository;
        this.contatoMapper = contatoMapper;
        this.interacaoRepository = interacaoRepository;
        this.leadDetalheMapper = leadDetalheMapper;
        this.responsavelMapper = responsavelMapper;
        this.funilMapper = funilMapper;
        this.cadenciaMapper = cadenciaMapper;
    }

    public LeadDetalhadoResponseDTO buscar(Integer id) {
        Lead lead = leadService.listarId(id);

        LeadDetalhadoResponseDTO detalhe = new LeadDetalhadoResponseDTO();
        detalhe.setLead(cadastroLeadMapper.toResponse(lead));
        detalhe.setContatos(contatoRepository.findByLead_IdOrderByIdAsc(id).stream()
                .map(contatoMapper::toResponse)
                .toList());
        detalhe.setInteracoes(interacaoRepository.findByLead_IdOrderByDataHoraDesc(id).stream()
                .map(leadDetalheMapper::toTimeline)
                .toList());
        detalhe.setResponsavel(responsavelMapper.toResponsavel(lead));
        detalhe.setFunil(funilMapper.toFunil(lead, id));
        detalhe.setCadencia(cadenciaMapper.toCadencia(id));

        return detalhe;
    }
}
