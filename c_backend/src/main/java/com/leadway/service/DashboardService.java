package com.leadway.service;

import com.leadway.dto.response.*;
import com.leadway.entity.*;
import com.leadway.exception.EntidadeNaoEncontradaException;
import com.leadway.repository.*;
import com.leadway.entity.Interacao;
import com.leadway.entity.Cadencia;
import com.leadway.entity.LeadEtapaHistorico;
import com.leadway.entity.Lead;
import com.leadway.entity.StatusTarefa;
import com.leadway.entity.Tarefa;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.CadenciaRepository;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final CadenciaRepository cadenciaRepository;
    private final TarefaRepository tarefaRepository;
    private final LeadEtapaHistoricoRepository leadEtapaHistoricoRepository;
    private final InteracaoRepository interacaoRepository;
    private final CadenciaRepository cadenciaRepository;

    public DashboardService(
            LeadRepository leadRepository,
            CadenciaRepository cadenciaRepository,
            TarefaRepository tarefaRepository,
            LeadEtapaHistoricoRepository leadEtapaHistoricoRepository,
            InteracaoRepository interacaoRepository, CadenciaRepository cadenciaRepository) {
        this.leadRepository = leadRepository;
        this.cadenciaRepository = cadenciaRepository;
        this.tarefaRepository = tarefaRepository;
        this.leadEtapaHistoricoRepository = leadEtapaHistoricoRepository;
        this.interacaoRepository = interacaoRepository;
        this.cadenciaRepository = cadenciaRepository;
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

        Integer leadsSemAtualizacao = contarCadenciasVencidas(idVendedor);

        return new DashboardIndicadoresResponseDTO(
                totalLeads,
                compromissosSemana,
                leadsEmProspeccao,
                leadsSemAtualizacao
        );
    }

    /** Leads ativos com cadência ativa cujo último contato ou mudança de etapa venceu. */
    public int contarCadenciasVencidas(Integer idVendedor) {
        LocalDateTime agora = LocalDateTime.now();
        List<Cadencia> cadencias = cadenciaRepository.findAll();
        List<LeadEtapaHistorico> historico = leadEtapaHistoricoRepository.findAll();
        List<Interacao> interacoes = interacaoRepository.findAll();
        java.util.Set<Integer> idsVencidos = new java.util.HashSet<>();
        for (Cadencia cadencia : cadencias) {
            Lead lead = cadencia.getLead();
            if (lead == null || lead.getId() == null || !Boolean.TRUE.equals(lead.getAtivo())
                    || lead.getVendedor() == null || !idVendedor.equals(lead.getVendedor().getId())
                    || !"ATIVA".equalsIgnoreCase(cadencia.getStatus())) continue;
            if (cadencia.getDataInicio() != null && cadencia.getDataInicio().isAfter(agora)) continue;
            if (cadencia.getDataFim() != null && cadencia.getDataFim().isBefore(agora)) { idsVencidos.add(lead.getId()); continue; }
            LocalDateTime referencia = cadencia.getDataInicio() != null ? cadencia.getDataInicio() : lead.getCriadoEm();
            if (referencia == null) continue;
            for (Interacao interacao : interacoes) {
                if (interacao.getLead() == null || !lead.getId().equals(interacao.getLead().getId())) continue;
                LocalDateTime data = interacao.getDataHora() != null ? interacao.getDataHora() : interacao.getCriadoEm();
                if (data != null && data.isAfter(referencia)) referencia = data;
            }
            for (LeadEtapaHistorico movimento : historico) {
                if (movimento.getLead() != null && lead.getId().equals(movimento.getLead().getId())
                        && movimento.getCriadoEm() != null && movimento.getCriadoEm().isAfter(referencia)) {
                    referencia = movimento.getCriadoEm();
                }
            }
            LocalDateTime prazo = switch (cadencia.getFrequencia().toUpperCase(java.util.Locale.ROOT)) {
                case "DIARIA" -> referencia.plusDays(1);
                case "SEMANAL" -> referencia.plusDays(7);
                case "QUINZENAL" -> referencia.plusDays(15);
                case "MENSAL" -> referencia.plusMonths(1);
                case "PERSONALIZADA" -> cadencia.getIntervaloDias() != null && cadencia.getIntervaloDias() > 0
                        ? referencia.plusDays(cadencia.getIntervaloDias()) : null;
                default -> null;
            };
            if (prazo != null && prazo.isBefore(agora)) idsVencidos.add(lead.getId());
        }
        return idsVencidos.size();
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

            Optional<Cadencia> cadenciaOpt = cadenciaRepository.findByLead_IdAndStatus(
                    lead.getId(),
                    StatusCadencia.ATIVA
            );

            if (cadenciaOpt.isEmpty()) {
                continue;
            }

            Cadencia cadencia = cadenciaOpt.get();

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

            LocalDateTime dataReferencia = ultimoContato;

            if (dataReferencia == null) {
                dataReferencia = cadencia.getDataInicio();
            }

            if (dataReferencia == null) {
                continue;
            }

            LocalDateTime proximoContato = calcularProximoContato(dataReferencia, cadencia);

            if (proximoContato == null) {
                continue;
            }

            if (!proximoContato.isBefore(horarioAtual)) {
                continue;
            }

            Long minutosAtraso = Duration.between(proximoContato, horarioAtual).toMinutes();

            String descricaoCadencia = formatarAtraso(minutosAtraso);

            String etapa = null;

            LeadAtencaoResponseDTO dto = new LeadAtencaoResponseDTO(
                    lead.getId(),
                    lead.getNomeFantasia(),
                    etapa,
                    lead.getPrioridade(),
                    descricaoCadencia,
                    ultimoContato
            );

            resultado.add(dto);

        }
        return resultado;

    }

    private String formatarAtraso(Long minutosAtraso) {
        Long dias = minutosAtraso / (24*60);

        if (dias == 0) {
            return "Atrasado há menos de um dia.";
        }

        if (dias == 1) {
            return "Atrasado há 1 dia";
        }

        return "Atrasado há " + dias  + " dias.";
    }

    private LocalDateTime calcularProximoContato(
            LocalDateTime ultimoContato,
            Cadencia cadencia) {

        FrequenciaCadencia frequencia = cadencia.getFrequencia();

        switch (frequencia) {

            case DIARIA:
                return ultimoContato.plusDays(1);

            case SEMANAL:
                return ultimoContato.plusWeeks(1);

            case QUINZENAL:
                return ultimoContato.plusDays(15);

            case MENSAL:
                return ultimoContato.plusMonths(1);

            case PERSONALIZADA:

                Integer intervaloDias = cadencia.getIntervaloDias();

                if (intervaloDias == null || intervaloDias <= 0) {
                    return null;
                }

                return ultimoContato.plusDays(intervaloDias);

            default:
                return null;
        }
    }

}
