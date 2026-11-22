package com.leadway.service;

import com.leadway.dto.request.InteracaoRequestDTO;
import com.leadway.dto.request.NotaLeadRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Etapa;
import com.leadway.entity.Interacao;
import com.leadway.entity.Lead;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.EtapaRepository;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Base do feature/teste-all (DTOs record + conversão inline), com os defaults
 * de anotação e a troca de etapa do feature/calcom portados.
 * A escrita do histórico de etapa tem um único dono: LeadService.registrarEtapa.
 */
@Service
public class InteracaoService {
    private final InteracaoRepository interacoes;
    private final LeadRepository leads;
    private final ContatoRepository contatos;
    private final EtapaRepository etapas;
    private final LeadService leadService;

    public InteracaoService(InteracaoRepository interacoes,
                            LeadRepository leads,
                            ContatoRepository contatos,
                            EtapaRepository etapas,
                            LeadService leadService) {
        this.interacoes = interacoes;
        this.leads = leads;
        this.contatos = contatos;
        this.etapas = etapas;
        this.leadService = leadService;
    }

    @Transactional(readOnly = true)
    public List<InteracaoResponseDTO> listar(Integer leadId) {
        buscarLead(leadId);
        return interacoes.findByLead_Id(leadId).stream()
                .sorted(Comparator.comparing((Interacao i) ->
                        i.getDataHora() != null ? i.getDataHora() : i.getCriadoEm(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::converter).toList();
    }

    @Transactional
    public InteracaoResponseDTO criar(Integer leadId, InteracaoRequestDTO dto) {
        Lead lead = buscarLead(leadId);
        Interacao interacao = new Interacao();
        interacao.setLead(lead);
        interacao.setUsuario(lead.getVendedor());
        preencher(interacao, leadId, dto);
        return converter(interacoes.saveAndFlush(interacao));
    }

    /**
     * Endpoint /leads/{leadId}/notes do feature/calcom adaptado ao modelo do
     * feature/teste-all: Contato permanece @Transient, usuario substitui vendedor
     * e a resposta é o record InteracaoResponseDTO.
     */
    @Transactional
    public InteracaoResponseDTO cadastrarNota(Integer leadId, NotaLeadRequestDTO dto) {
        Lead lead = buscarLead(leadId);
        Contato contato = resolverContato(leadId, dto.getContatoId());

        Interacao interacao = new Interacao();
        interacao.setLead(lead);
        interacao.setUsuario(lead.getVendedor());
        interacao.setContato(contato);
        String tipo = dto.getTipo();
        interacao.setTipo(tipo == null || tipo.isBlank() ? "ANOTACAO" : tipo.trim());
        interacao.setDataHora(dto.getDataHora() != null ? dto.getDataHora() : LocalDateTime.now());
        interacao.setDescricao(dto.getDescricao());

        aplicarEtapa(lead, dto.getEtapaId());

        return converter(interacoes.saveAndFlush(interacao));
    }

    @Transactional
    public InteracaoResponseDTO atualizar(Integer leadId, Integer id, InteracaoRequestDTO dto) {
        Interacao interacao = buscarInteracao(leadId, id);
        preencher(interacao, leadId, dto);
        return converter(interacoes.saveAndFlush(interacao));
    }

    @Transactional
    public void excluir(Integer leadId, Integer id) {
        interacoes.delete(buscarInteracao(leadId, id));
    }

    private void aplicarEtapa(Lead lead, Integer etapaId) {
        if (etapaId == null) return;
        Etapa novaEtapa = etapas.findById(etapaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Etapa não encontrada"));
        if (lead.getEtapa() == null || !lead.getEtapa().getId().equals(novaEtapa.getId())) {
            lead.setEtapa(novaEtapa);
            leadService.registrarEtapa(lead);
        }
    }

    private Lead buscarLead(Integer id) {
        return leads.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead não encontrado"));
    }

    private Interacao buscarInteracao(Integer leadId, Integer id) {
        return interacoes.findById(id).filter(i -> i.getLead() != null && leadId.equals(i.getLead().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interação não encontrada neste Lead"));
    }

    private Contato resolverContato(Integer leadId, Integer contatoId) {
        if (contatoId == null) return null;
        Contato contato = contatos.findById(contatoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
        if (contato.getLead() == null || !leadId.equals(contato.getLead().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O contato não pertence a este Lead");
        return contato;
    }

    private void preencher(Interacao interacao, Integer leadId, InteracaoRequestDTO dto) {
        interacao.setContato(resolverContato(leadId, dto.contatoId()));
        interacao.setTipo(dto.tipo().trim());
        interacao.setDescricao(dto.descricao().trim());
        interacao.setDataHora(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());
    }

    private InteracaoResponseDTO converter(Interacao i) {
        return new InteracaoResponseDTO(i.getId(), i.getLead().getId(),
                i.getContato() == null ? null : i.getContato().getId(),
                i.getContato() == null ? null : i.getContato().getNome(),
                i.getTipo(), i.getDataHora(), i.getDescricao(), i.getCriadoEm(), i.getAtualizadoEm());
    }
}
