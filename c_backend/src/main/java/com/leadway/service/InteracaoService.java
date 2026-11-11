package com.leadway.service;

import com.leadway.dto.request.CadastroInteracaoRequestDTO;
import com.leadway.entity.*;
import com.leadway.exception.EntidadeNaoEncontradaException;
import com.leadway.exception.LeadNaoEncontradoException;
import com.leadway.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteracaoService {

    private final InteracaoRepository interacaoRepository;
    private final LeadRepository leadRepository;
    private final VendedorRepository vendedorRepository;
    private final EtapaRepository etapaRepository;
    private final LeadEtapaHistoricoRepository historicoRepository;

    public InteracaoService(InteracaoRepository interacaoRepository, LeadRepository leadRepository, VendedorRepository vendedorRepository, EtapaRepository etapaRepository, LeadEtapaHistoricoRepository historicoRepository) {
        this.interacaoRepository = interacaoRepository;
        this.leadRepository = leadRepository;
        this.vendedorRepository = vendedorRepository;
        this.etapaRepository = etapaRepository;
        this.historicoRepository = historicoRepository;
    }

    public Interacao cadastrar(Integer leadId, CadastroInteracaoRequestDTO dto) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNaoEncontradoException("Lead não consta nos registros"));

        Vendedor vendedor = lead.getVendedor();

        Interacao interacao = new Interacao();
        interacao.setLead(lead);
        interacao.setVendedor(vendedor);
        interacao.setTitulo(dto.getTitulo());
        interacao.setTipo(dto.getTipo());
        interacao.setDataHora(dto.getDataHora());
        interacao.setDescricao(dto.getDescricao());

        Etapa novaEtapa = etapaRepository.findById(dto.getEtapaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Etapa não consta nos registros"));

        if (!lead.getEtapa().getId().equals(novaEtapa.getId())) {
            lead.setEtapa(novaEtapa);

            LeadEtapaHistorico historico = new LeadEtapaHistorico();
            historico.setLead(lead);
            historico.setEtapa(novaEtapa);

            historicoRepository.save(historico);
        }

        return interacaoRepository.save(interacao);
    }

    public List<Interacao> listarPorLead(Integer leadId) {

        if (!leadRepository.existsById(leadId)) {
            throw new EntidadeNaoEncontradaException("Lead não consta nos registros");
        }

        return interacaoRepository.findByLead_IdOrderByDataHoraDesc(leadId);
    }

    public void excluir(Integer leadId, Integer id) {

        if (!leadRepository.existsById(leadId)) {
            throw new EntidadeNaoEncontradaException("Lead não consta nos registros");
        }

        if (!interacaoRepository.existsByIdAndLead_Id(id, leadId)) {
           throw new EntidadeNaoEncontradaException("Interação não consta nos registros desse lead");
        }

        interacaoRepository.deleteById(id);
    }
}
