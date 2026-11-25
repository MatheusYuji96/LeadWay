package com.leadway.service;

import com.leadway.dto.response.LeadResponseDTO;
import com.leadway.dto.response.VendedorDetalheResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadEtapaHistoricoRepository;
import com.leadway.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Monta o agregado devolvido por GET /vendedores/{id}.
 * Consultas reaproveitadas de LeadRepository, InteracaoRepository e
 * LeadEtapaHistoricoRepository; nenhuma escrita é realizada.
 */
@Service
public class VendedorDetalheService {

    private static final int DIAS_PARADO = 7;
    private static final int DIAS_CONTATOS = 30;
    private static final int ORDEM_ETAPA_PROPOSTA = 3;
    private static final int ORDEM_ETAPA_FECHADA = 5;
    private static final int MESES_EVOLUCAO = 6;
    private static final String[] MESES = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    private final VendedorService vendedorService;
    private final LeadRepository leadRepository;
    private final InteracaoRepository interacaoRepository;
    private final LeadEtapaHistoricoRepository leadEtapaHistoricoRepository;
    private final CadastroLeadMapper cadastroLeadMapper;

    public VendedorDetalheService(VendedorService vendedorService,
                                  LeadRepository leadRepository,
                                  InteracaoRepository interacaoRepository,
                                  LeadEtapaHistoricoRepository leadEtapaHistoricoRepository,
                                  CadastroLeadMapper cadastroLeadMapper) {
        this.vendedorService = vendedorService;
        this.leadRepository = leadRepository;
        this.interacaoRepository = interacaoRepository;
        this.leadEtapaHistoricoRepository = leadEtapaHistoricoRepository;
        this.cadastroLeadMapper = cadastroLeadMapper;
    }

    @Transactional(readOnly = true)
    public VendedorDetalheResponseDTO buscar(Integer id) {
        Vendedor vendedor = vendedorService.buscar(id);
        LocalDateTime agora = LocalDateTime.now();

        int total = nuloComoZero(leadRepository.countByVendedor_IdAndAtivoTrue(id));
        int concluidos = paraInteiro(
                leadRepository.countByVendedor_IdAndAtivoTrueAndEtapa_Ordem(id, ORDEM_ETAPA_PROPOSTA));
        int convertidos = paraInteiro(
                leadRepository.countByVendedor_IdAndAtivoTrueAndEtapa_Ordem(id, ORDEM_ETAPA_FECHADA));
        int semVisita = nuloComoZero(
                leadRepository.countByVendedor_IdAndAtivoTrueAndDataPrimeiraVisitaIsNull(id));
        int visitas = Math.max(total - semVisita, 0);

        List<Lead> carteira = leadRepository.findByVendedor_IdAndAtivoTrue(id);
        int novos = (int) carteira.stream()
                .filter(lead -> lead.getCriadoEm() != null
                        && lead.getCriadoEm().isAfter(agora.minusDays(DIAS_CONTATOS)))
                .count();

        int contatos = paraInteiro(interacaoRepository.contarVendedorNoPeriodo(
                id, agora.minusDays(DIAS_CONTATOS), agora.plusDays(1)));
        int contatosAnteriores = paraInteiro(interacaoRepository.contarVendedorNoPeriodo(
                id, agora.minusDays(2L * DIAS_CONTATOS), agora.minusDays(DIAS_CONTATOS)));

        long pendentes = paraLongo(leadRepository.contarParadosPorVendedor(
                id, agora.minusDays(DIAS_PARADO), ORDEM_ETAPA_FECHADA));

        VendedorDetalheResponseDTO.NoticeDTO notice = pendentes > 0
                ? new VendedorDetalheResponseDTO.NoticeDTO(
                        "Este representante possui " + pendentes + " leads pendentes.",
                        "Existem oportunidades sem atualização há mais de " + DIAS_PARADO + " dias.")
                : null;

        double taxaConversao = total == 0 ? 0 : (convertidos * 100.0) / total;
        List<VendedorDetalheResponseDTO.StatDTO> stats = List.of(
                new VendedorDetalheResponseDTO.StatDTO(
                        "wallet", "briefcase", "Leads em carteira", String.valueOf(total),
                        novos + " novos este mês", novos > 0 ? "↑ " + novos : "—", "brand"),
                new VendedorDetalheResponseDTO.StatDTO(
                        "contacts", "phone", "Contatos realizados", String.valueOf(contatos),
                        "nos últimos " + DIAS_CONTATOS + " dias", variacao(contatos, contatosAnteriores), "blue"),
                new VendedorDetalheResponseDTO.StatDTO(
                        "visits", "calendar", "Visitas", String.valueOf(visitas),
                        "com visita registrada", String.valueOf(visitas), "purple"),
                new VendedorDetalheResponseDTO.StatDTO(
                        "conversions", "trend", "Conversões", String.valueOf(convertidos),
                        String.format(Locale.ROOT, "%.1f%% de conversão", taxaConversao), "—", "warning")
        );

        List<VendedorDetalheResponseDTO.EvolutionDTO> evolution = montarEvolucao(id, agora);
        List<LeadResponseDTO> leads = carteira.stream()
                .map(cadastroLeadMapper::toResponse)
                .toList();

        return new VendedorDetalheResponseDTO(
                new VendedorDetalheResponseDTO.RepresentanteDTO(
                        vendedor.getId(),
                        vendedor.getNome(),
                        vendedor.getCodVendedor(),
                        vendedor.getRegiao(),
                        vendedor.getEmail(),
                        vendedor.getTelefone(),
                        total,
                        concluidos,
                        (int) pendentes,
                        vendedor.getAtivo()),
                notice,
                stats,
                evolution,
                leads);
    }

    private List<VendedorDetalheResponseDTO.EvolutionDTO> montarEvolucao(Integer vendedorId, LocalDateTime agora) {
        YearMonth primeiroMes = YearMonth.from(agora.toLocalDate()).minusMonths(MESES_EVOLUCAO - 1);
        Map<YearMonth, Integer> porMes = new LinkedHashMap<>();
        for (int i = 0; i < MESES_EVOLUCAO; i++) {
            porMes.put(primeiroMes.plusMonths(i), 0);
        }

        LocalDateTime inicio = primeiroMes.atDay(1).atStartOfDay();
        LocalDateTime fim = primeiroMes.plusMonths(MESES_EVOLUCAO).atDay(1).atStartOfDay();
        for (Object[] linha : leadEtapaHistoricoRepository.contarAtividadesPorDia(vendedorId, inicio, fim)) {
            if (linha == null || linha.length < 3 || linha[2] == null) {
                continue;
            }
            YearMonth mes = paraAnoMes(linha[0]);
            if (mes != null) {
                porMes.merge(mes, ((Number) linha[2]).intValue(), Integer::sum);
            }
        }

        List<VendedorDetalheResponseDTO.EvolutionDTO> evolucao = new ArrayList<>();
        porMes.forEach((mes, valor) ->
                evolucao.add(new VendedorDetalheResponseDTO.EvolutionDTO(
                        MESES[mes.getMonthValue() - 1], valor)));
        return evolucao;
    }

    private YearMonth paraAnoMes(Object valor) {
        if (valor instanceof Date sqlDate) {
            return YearMonth.from(sqlDate.toLocalDate());
        }
        if (valor instanceof LocalDate localDate) {
            return YearMonth.from(localDate);
        }
        if (valor instanceof java.util.Date utilDate) {
            return YearMonth.from(utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (valor instanceof CharSequence texto && texto.length() >= 7) {
            return YearMonth.parse(texto.subSequence(0, 7));
        }
        return null;
    }

    private String variacao(int atual, int anterior) {
        if (anterior == 0) {
            return atual > 0 ? "novo" : "—";
        }
        double percentual = ((atual - anterior) * 100.0) / anterior;
        return String.format(Locale.ROOT, "%+.0f%%", percentual);
    }

    private int nuloComoZero(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private int paraInteiro(Long valor) {
        return valor == null ? 0 : valor.intValue();
    }

    private long paraLongo(Long valor) {
        return valor == null ? 0L : valor;
    }
}
