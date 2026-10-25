package com.leadway.service;

import com.leadway.dto.response.*;
import com.leadway.entity.Interacao;
import com.leadway.entity.Lead;
import com.leadway.entity.StatusTarefa;
import com.leadway.entity.Tarefa;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final TarefaRepository tarefaRepository;
    private final LeadEtapaHistoricoRepository leadEtapaHistoricoRepository;
    private final InteracaoRepository interacaoRepository;

    public DashboardService(
            LeadRepository leadRepository,
            TarefaRepository tarefaRepository,
            LeadEtapaHistoricoRepository leadEtapaHistoricoRepository,
            InteracaoRepository interacaoRepository) {
        this.leadRepository = leadRepository;
        this.tarefaRepository = tarefaRepository;
        this.leadEtapaHistoricoRepository = leadEtapaHistoricoRepository;
        this.interacaoRepository = interacaoRepository;
    }

    public DashboardIndicadoresResponseDTO buscarIndicadores(Integer idVendedor) {

        Integer totalLeads = leadRepository.countByVendedor_IdAndAtivoTrue(idVendedor);

        LocalDate hoje = LocalDate.now();

        Integer diasDesdeSegunda = hoje.getDayOfWeek().getValue() - 1;

        LocalDate inicioDaSemana = hoje.minusDays(diasDesdeSegunda);
        LocalDate fimDaSemana = inicioDaSemana.plusWeeks(1);

        LocalDateTime inicio = inicioDaSemana.atStartOfDay();
        LocalDateTime fim = fimDaSemana.atStartOfDay();

        Integer compromissosSemana = tarefaRepository
                .countByLead_Vendedor_IdAndStatusAndDataPrazoGreaterThanEqualAndDataPrazoLessThan(
                        idVendedor,
                        StatusTarefa.ABERTA,
                        inicio,
                        fim
                );

        Integer leadsEmProspeccao = leadRepository
                .countByVendedor_IdAndAtivoTrueAndDataPrimeiraVisitaIsNull(idVendedor);

        Integer leadsSemAtualizacao = 3;

        return new DashboardIndicadoresResponseDTO(
                totalLeads,
                compromissosSemana,
                leadsEmProspeccao,
                leadsSemAtualizacao
        );
    }

    public List<ProximosCompromissosResponseDTO> buscarProximosCompromissos(Integer idVendedor) {
        LocalDateTime horarioAtual = LocalDateTime.now();

        List<Tarefa> tarefas = tarefaRepository
                .findTop3ByLead_Vendedor_IdAndStatusAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
                        idVendedor,
                        StatusTarefa.ABERTA,
                        horarioAtual
                );

        return tarefas
                .stream()
                .map(tarefa -> new ProximosCompromissosResponseDTO(
                        tarefa.getDataPrazo(),
                        tarefa.getLead().getNomeFantasia(),
                        tarefa.getDescricao()
                )).toList();
    }

    public AtividadesSemanaResponseDTO buscarAtividadesSemana(Integer idVendedor) {
        LocalDate hoje = LocalDate.now();

        Integer diasDesdeSegunda = hoje.getDayOfWeek().getValue() - 1;

        LocalDate inicioDaSemana = hoje.minusDays(diasDesdeSegunda);
        LocalDate fimDaSemana = inicioDaSemana.plusWeeks(1);

        List<Object[]> resultados = leadEtapaHistoricoRepository.contarAtividadesPorDia(
                idVendedor,
                inicioDaSemana.atStartOfDay(),
                fimDaSemana.atStartOfDay()
        );

       List<AtividadeDiariaResponseDTO> atividades = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioDaSemana.plusDays(i);

            int contatos = 0;
            int portfolios = 0;
            int followUps = 0;

            for (Object[] resultado : resultados) {

                LocalDate diaResultado = LocalDate.parse(
                        resultado[0].toString()
                );

                Integer etapaId = ((Number) resultado[1]).intValue();
                Integer quantidade = ((Number) resultado[2]).intValue();

                if (dia.equals(diaResultado)) {
                    if (etapaId == 2) {
                        contatos = quantidade;
                    } else if (etapaId == 3) {
                        portfolios = quantidade;
                    } else if (etapaId == 4) {
                        followUps = quantidade;
                    }
                }
            }

            atividades.add(new AtividadeDiariaResponseDTO(dia, contatos, portfolios, followUps));
        }

        return new AtividadesSemanaResponseDTO(inicioDaSemana, fimDaSemana.minusDays(1), atividades);
    }

    public List<LeadAtencaoResponseDTO> buscarLeadsAtencao(Integer idVendedor) {
        LocalDateTime horarioAtual = LocalDateTime.now();

        List<Lead> leads = leadRepository.findByVendedor_IdAndAtivoTrue(idVendedor);

        List<LeadAtencaoResponseDTO> resultado = new ArrayList<>();

        for (Lead lead : leads) {

            if (resultado.size() == 4) {
                break;
            }

            LocalDateTime ultimoContato = null;

            List<Interacao> interacoes = interacaoRepository.findByLead_Id(lead.getId());

            for (Interacao interacao : interacoes) {
                LocalDateTime dataInteracao = interacao.getDataHora();

                if (dataInteracao == null) {
                    dataInteracao = interacao.getCriadoEm();
                }

                if (dataInteracao != null) {
                    if (ultimoContato == null || dataInteracao.isAfter(ultimoContato)) {
                        ultimoContato = dataInteracao;
                    }
                }
            }

            Tarefa proximaTarefa = tarefaRepository
                    .findFirstByLead_IdAndStatusAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
                            lead.getId(),
                            StatusTarefa.ABERTA,
                            horarioAtual
                    );

            String etapa = null;

            if (lead.getEtapa() != null) {
                etapa = lead.getEtapa().getNome();
            }

            String proximaAcao = null;
            LocalDateTime dataProximaAcao = null;

            if (proximaTarefa != null) {
                proximaAcao = proximaTarefa.getDescricao();
                dataProximaAcao = proximaTarefa.getDataPrazo();
            }

            LeadAtencaoResponseDTO dto = new LeadAtencaoResponseDTO(
                    lead.getId(),
                    lead.getNomeFantasia(),
                    etapa,
                    ultimoContato,
                    proximaAcao,
                    dataProximaAcao
            );

            resultado.add(dto);

        }
        return resultado;

    }

}
