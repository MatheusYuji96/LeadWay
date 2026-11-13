package com.leadway.service;

import com.leadway.dto.request.AgendamentoRequestDTO;
import com.leadway.dto.response.AgendaResponseDTO;
import com.leadway.dto.response.CompromissoResponseDTO;
import com.leadway.dto.response.OverviewItemDTO;
import com.leadway.dto.response.ProximosCompromissosResponseDTO;
import com.leadway.dto.response.SlotDTO;
import com.leadway.dto.response.SlotsResponseDTO;
import com.leadway.entity.CalIntegracao;
import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import com.leadway.mapper.CompromissoMapper;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.LeadRepository;
import com.leadway.exception.CalException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AgendaService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    // Tipo de evento do Cal.com usado por TODOS os compromissos (30 minutos).
    // Toda conta nova do Cal.com costuma ter um evento de 30 min; confira no seu painel.
    private static final String SLUG_EVENTO = "30min";

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final CalIntegracaoService calIntegracaoService;
    private final CalApiService calApiService;
    private final CompromissoMapper compromissoMapper;
    private final LeadRepository leadRepository;
    private final ContatoRepository contatoRepository;

    public AgendaService(CalIntegracaoService calIntegracaoService,
                         CalApiService calApiService,
                         CompromissoMapper compromissoMapper,
                         LeadRepository leadRepository,
                         ContatoRepository contatoRepository) {
        this.calIntegracaoService = calIntegracaoService;
        this.calApiService = calApiService;
        this.compromissoMapper = compromissoMapper;
        this.leadRepository = leadRepository;
        this.contatoRepository = contatoRepository;
    }

    public boolean estaConectado(Integer vendedorId) {
        return calIntegracaoService.estaConectado(vendedorId);
    }

    // =====================================================================
    // GET /agenda  ->  compromissos de um mês
    // =====================================================================
    public AgendaResponseDTO listar(Integer vendedorId, String mes) {
        AgendaResponseDTO resposta = new AgendaResponseDTO();

        // Vendedor sem Cal.com conectado NÃO é erro: avisamos a tela, que mostra o convite.
        if (!calIntegracaoService.estaConectado(vendedorId)) {
            resposta.setConnected(false);
            resposta.setItems(new ArrayList<>());
            resposta.setOverview(new ArrayList<>());
            return resposta;
        }

        CalIntegracao credencial = calIntegracaoService.buscarCredencial(vendedorId);
        YearMonth anoMes = converterMes(mes);

        // Primeiro instante do mês até o primeiro instante do mês seguinte.
        Instant de = anoMes.atDay(1).atStartOfDay(FUSO).toInstant().minusSeconds(1);
        Instant ate = anoMes.plusMonths(1).atDay(1).atStartOfDay(FUSO).toInstant();

        List<CompromissoResponseDTO> itens = buscarCompromissos(credencial, de, ate);

        resposta.setConnected(true);
        resposta.setItems(itens);
        resposta.setOverview(montarResumo(itens, anoMes));
        return resposta;
    }

    // =====================================================================
    // GET /agenda/slots  ->  horários livres de um dia
    // =====================================================================
    public SlotsResponseDTO buscarHorariosLivres(Integer vendedorId, String data) {
        CalIntegracao credencial = calIntegracaoService.buscarCredencial(vendedorId);
        LocalDate dia = converterDia(data);

        Instant inicio = dia.atStartOfDay(FUSO).toInstant();
        Instant fim = dia.plusDays(1).atStartOfDay(FUSO).toInstant();

        List<String> horarios = calApiService.buscarHorariosLivres(
                credencial.getCalApiKey(), credencial.getCalUsername(), SLUG_EVENTO, inicio, fim, FUSO.getId());

        List<SlotDTO> slots = new ArrayList<>();
        Instant agora = Instant.now();
        for (String horario : horarios) {
            Instant instante = compromissoMapper.lerInstante(horario);
            // Horário que já passou não adianta mostrar.
            if (instante == null || instante.isBefore(agora)) {
                continue;
            }
            SlotDTO slot = new SlotDTO();
            slot.setStart(horario);                                   // volta intacto no POST
            slot.setLabel(HORA.format(instante.atZone(FUSO)));        // ex.: 09:30
            slots.add(slot);
        }

        SlotsResponseDTO resposta = new SlotsResponseDTO();
        resposta.setDate(dia.toString());
        resposta.setSlots(slots);
        return resposta;
    }

    // =====================================================================
    // POST /appointments  ->  criar compromisso
    // =====================================================================
    public CompromissoResponseDTO criar(AgendamentoRequestDTO dto) {
        // 1) Validar o que veio da tela
        if (dto.getVendedorId() == null || dto.getLeadId() == null || dto.getContatoId() == null) {
            throw CalException.invalido("Selecione a empresa e o contato.");
        }
        if (dto.getSlotStart() == null || dto.getSlotStart().isBlank()) {
            throw CalException.invalido("Selecione um horário.");
        }
        if (dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw CalException.invalido("Selecione o tipo de compromisso.");
        }

        CalIntegracao credencial = calIntegracaoService.buscarCredencial(dto.getVendedorId());

        // 2) Buscar empresa e contato NO NOSSO BANCO (não confiamos em e-mail vindo da tela)
        Optional<Lead> leadOpt = leadRepository.findById(dto.getLeadId());
        if (leadOpt.isEmpty()) {
            throw CalException.invalido("Empresa não encontrada.");
        }
        Lead lead = leadOpt.get();

        Optional<Contato> contatoOpt = contatoRepository.findById(dto.getContatoId());
        if (contatoOpt.isEmpty()) {
            throw CalException.invalido("Contato não encontrado.");
        }
        Contato contato = contatoOpt.get();

        if (contato.getLead() == null || !contato.getLead().getId().equals(lead.getId())) {
            throw CalException.invalido("O contato selecionado não pertence a esta empresa.");
        }
        if (contato.getEmail() == null || contato.getEmail().isBlank()) {
            throw CalException.invalido("O contato não tem e-mail, e o convite é enviado por e-mail.");
        }

        // 3) Validar o horário escolhido
        Instant inicio = compromissoMapper.lerInstante(dto.getSlotStart());
        if (inicio == null) {
            throw CalException.invalido("Horário inválido. Escolha um dos horários livres.");
        }
        if (inicio.isBefore(Instant.now())) {
            throw CalException.invalido("Esse horário já passou. Escolha outro.");
        }

        // 4) O "post-it" que fica colado no booking dentro do Cal.com.
        //    É por ele que, na hora de listar, sabemos de qual empresa é a reunião.
        Map<String, String> metadata = new HashMap<>();
        metadata.put(CompromissoMapper.META_ORIGEM, "leadway");
        metadata.put(CompromissoMapper.META_LEAD, String.valueOf(lead.getId()));
        metadata.put(CompromissoMapper.META_CONTATO, String.valueOf(contato.getId()));
        metadata.put(CompromissoMapper.META_TIPO, dto.getTipo().trim());

        // 5) Pedir ao Cal.com para criar (ele grava no calendário e manda o e-mail)
        Map<String, Object> booking = calApiService.criarBooking(
                credencial.getCalApiKey(), credencial.getCalUsername(), SLUG_EVENTO, inicio,
                contato.getNome(), contato.getEmail(), FUSO.getId(), metadata, dto.getNotas());

        // 6) Traduzir o booking criado para o DTO e devolver à tela
        CompromissoResponseDTO resposta = compromissoMapper.toResponse(booking, lead.getNomeFantasia(), FUSO);
        if (resposta == null) {
            throw CalException.indisponivel();
        }
        return resposta;
    }

    // =====================================================================
    // DELETE /appointments/{uid}  ->  cancelar compromisso
    // =====================================================================
    public void cancelar(Integer vendedorId, String uid, String motivo) {
        if (uid == null || uid.isBlank()) {
            throw CalException.invalido("Identificador do compromisso inválido.");
        }
        CalIntegracao credencial = calIntegracaoService.buscarCredencial(vendedorId);
        // Só o dono da chave consegue cancelar o booking: o próprio Cal.com garante isso.
        calApiService.cancelarBooking(credencial.getCalApiKey(), uid, motivo);
    }

    // =====================================================================
    // Usados pelo Dashboard
    // =====================================================================

    /** Próximos compromissos (a partir de agora, olhando 30 dias à frente). */
    public List<ProximosCompromissosResponseDTO> proximosCompromissos(Integer vendedorId, int limite) {
        CalIntegracao credencial = calIntegracaoService.buscarCredencial(vendedorId);
        Instant agora = Instant.now();
        List<CompromissoResponseDTO> todos = buscarCompromissos(credencial, agora, agora.plus(Duration.ofDays(30)));

        LocalDateTime agoraLocal = LocalDateTime.now(FUSO);
        List<ProximosCompromissosResponseDTO> proximos = new ArrayList<>();
        for (CompromissoResponseDTO c : todos) {
            LocalDateTime horario = LocalDateTime.of(LocalDate.parse(c.getDate()), LocalTime.parse(c.getStartTime()));
            if (horario.isBefore(agoraLocal)) {
                continue;
            }
            String descricao = c.getType();
            if (c.getContactName() != null) {
                descricao = c.getType() + " com " + c.getContactName();
            }
            proximos.add(new ProximosCompromissosResponseDTO(horario, c.getCompanyName(), descricao));
        }

        // Ordena do mais cedo para o mais tarde (o Cal.com não garante a ordem).
        Collections.sort(proximos, (a, b) -> a.getHorario().compareTo(b.getHorario()));
        if (proximos.size() > limite) {
            return new ArrayList<>(proximos.subList(0, limite));
        }
        return proximos;
    }

    /** Quantos compromissos o vendedor tem de segunda a domingo da semana atual. */
    public int contarCompromissosDaSemana(Integer vendedorId) {
        CalIntegracao credencial = calIntegracaoService.buscarCredencial(vendedorId);
        LocalDate segunda = LocalDate.now(FUSO).with(DayOfWeek.MONDAY);
        Instant de = segunda.atStartOfDay(FUSO).toInstant().minusSeconds(1);
        Instant ate = segunda.plusWeeks(1).atStartOfDay(FUSO).toInstant();
        return buscarCompromissos(credencial, de, ate).size();
    }

    // =====================================================================
    // Métodos privados (ajudantes)
    // =====================================================================

    /** Pergunta ao Cal.com e devolve só os compromissos "vivos" (não cancelados), já como DTO. */
    private List<CompromissoResponseDTO> buscarCompromissos(CalIntegracao credencial, Instant de, Instant ate) {
        List<Map<String, Object>> bookings = calApiService.listarBookings(credencial.getCalApiKey(), de, ate);

        List<CompromissoResponseDTO> itens = new ArrayList<>();
        for (Map<String, Object> booking : bookings) {
            Object status = booking.get("status");
            boolean ativo = "accepted".equals(status) || "pending".equals(status);
            if (!ativo) {
                continue;   // cancelados e recusados não aparecem na agenda
            }

            CompromissoResponseDTO dto = compromissoMapper.toResponse(booking, descobrirNomeEmpresa(booking), FUSO);
            if (dto != null) {
                itens.add(dto);
            }
        }
        return itens;
    }

    /** Usa o id da empresa (escrito no metadata) para buscar o nome no nosso banco. */
    private String descobrirNomeEmpresa(Map<String, Object> booking) {
        Integer leadId = compromissoMapper.lerLeadId(booking);
        if (leadId == null) {
            return "Agendado direto no Cal.com";   // reunião criada fora do LeadWay
        }
        Optional<Lead> leadOpt = leadRepository.findById(leadId);
        if (leadOpt.isEmpty()) {
            return "Empresa #" + leadId;
        }
        return leadOpt.get().getNomeFantasia();
    }

    private List<OverviewItemDTO> montarResumo(List<CompromissoResponseDTO> itens, YearMonth mes) {
        List<OverviewItemDTO> cartoes = new ArrayList<>();
        LocalDate hoje = LocalDate.now(FUSO);

        // O cartão "HOJE" só faz sentido quando o mês exibido é o mês atual.
        if (YearMonth.from(hoje).equals(mes)) {
            int deHoje = 0;
            for (CompromissoResponseDTO item : itens) {
                if (item.getDate().equals(hoje.toString())) {
                    deHoje++;
                }
            }
            cartoes.add(criarCartao("today", "calendar", "brand", "HOJE", textoQuantidade(deHoje)));
        }
        cartoes.add(criarCartao("month", "users", "warning", "NO MÊS", textoQuantidade(itens.size())));
        return cartoes;
    }

    private OverviewItemDTO criarCartao(String key, String icon, String tone, String label, String value) {
        OverviewItemDTO cartao = new OverviewItemDTO();
        cartao.setKey(key);
        cartao.setIcon(icon);
        cartao.setTone(tone);
        cartao.setLabel(label);
        cartao.setValue(value);
        return cartao;
    }

    private String textoQuantidade(int quantidade) {
        if (quantidade == 1) {
            return "1 compromisso";
        }
        return quantidade + " compromissos";
    }

    private YearMonth converterMes(String mes) {
        if (mes == null || mes.isBlank()) {
            return YearMonth.now(FUSO);   // sem mês informado: usa o mês atual
        }
        try {
            return YearMonth.parse(mes);  // espera "2026-11"
        } catch (DateTimeParseException e) {
            throw CalException.invalido("Mês inválido. Use o formato AAAA-MM.");
        }
    }

    private LocalDate converterDia(String data) {
        try {
            return LocalDate.parse(data);  // espera "2026-11-03"
        } catch (DateTimeParseException | NullPointerException e) {
            throw CalException.invalido("Data inválida. Use o formato AAAA-MM-DD.");
        }
    }
}
