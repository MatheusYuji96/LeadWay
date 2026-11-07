package com.leadway.service;

import com.leadway.dto.request.InteracaoRequestDTO;
import com.leadway.dto.response.InteracaoResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Interacao;
import com.leadway.entity.Lead;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class InteracaoService {
    private final InteracaoRepository interacoes;
    private final LeadRepository leads;
    private final ContatoRepository contatos;

    public InteracaoService(InteracaoRepository interacoes, LeadRepository leads, ContatoRepository contatos) {
        this.interacoes = interacoes;
        this.leads = leads;
        this.contatos = contatos;
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
        preencher(interacao, leadId, dto);
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

    private Lead buscarLead(Integer id) {
        return leads.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead não encontrado"));
    }

    private Interacao buscarInteracao(Integer leadId, Integer id) {
        return interacoes.findById(id).filter(i -> i.getLead() != null && leadId.equals(i.getLead().getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interação não encontrada neste Lead"));
    }

    private void preencher(Interacao interacao, Integer leadId, InteracaoRequestDTO dto) {
        Contato contato = null;
        if (dto.contatoId() != null) {
            contato = contatos.findById(dto.contatoId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
            if (contato.getLead() == null || !leadId.equals(contato.getLead().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O contato não pertence a este Lead");
            }
        }
        interacao.setContato(contato);
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
