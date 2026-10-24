package com.leadway.service;

import com.leadway.dto.response.AtividadeDiariaResponseDTO;
import com.leadway.dto.response.AtividadesSemanaResponseDTO;
import com.leadway.dto.response.DashboardIndicadoresResponseDTO;
import com.leadway.dto.response.ProximosCompromissosResponseDTO;
import com.leadway.entity.Tarefa;
import com.leadway.repository.CadastroLeadRepository;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import com.leadway.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final CadastroLeadRepository leadRepository;
    private final TarefaRepository tarefaRepository;
    private final LeadEtapaHistoricoRepository leadEtapaHistoricoRepository;

    public DashboardService(
            CadastroLeadRepository leadRepository,
            TarefaRepository tarefaRepository,
            LeadEtapaHistoricoRepository leadEtapaHistoricoRepository) {
        this.leadRepository = leadRepository;
        this.tarefaRepository = tarefaRepository;
        this.leadEtapaHistoricoRepository = leadEtapaHistoricoRepository;
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
                .countByLead_Vendedor_IdAndStatus_AbertaAndDataPrazoGreaterThanEqualAndDataPrazoLessThan(
                        idVendedor,
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
                .findTop3ByLead_Vendedor_IdAndStatus_AbertaAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
                        idVendedor,
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

}
