package com.leadway.mapper;

import com.leadway.dto.response.CompromissoResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CompromissoMapper {

    // Nomes das "anotações" (metadata) que o LeadWay escreve em cada booking que ele cria.
    // É assim que, depois, descobrimos de qual empresa/contato/tipo é a reunião.
    public static final String META_ORIGEM = "origem";
    public static final String META_LEAD = "leadwayLeadId";
    public static final String META_CONTATO = "leadwayContatoId";
    public static final String META_TIPO = "leadwayTipo";

    private static final String TIPO_PADRAO = "Reunião";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * @param booking     o compromisso, como o Cal.com devolveu
     * @param nomeEmpresa nome da empresa (o Service busca no banco e passa pronto)
     * @param fuso        fuso horário em que a tela deve mostrar as horas
     * @return o DTO, ou null se o booking não tiver horário de início
     */
    public CompromissoResponseDTO toResponse(Map<String, Object> booking, String nomeEmpresa, ZoneId fuso) {
        Instant inicio = lerInstante(booking.get("start"));
        if (inicio == null) {
            return null;
        }
        Instant fim = lerInstante(booking.get("end"));
        if (fim == null) {
            fim = inicio;
        }

        Map<String, Object> metadata = comoObjeto(booking.get("metadata"));
        Map<String, Object> campos = comoObjeto(booking.get("bookingFieldsResponses"));
        Map<String, Object> convidado = primeiroConvidado(booking.get("attendees"));

        String tipo = texto(metadata.get(META_TIPO));
        if (tipo == null) {
            tipo = TIPO_PADRAO;   // booking criado direto no Cal.com, sem passar pelo LeadWay
        }

        // O Cal.com trabalha em UTC. Aqui convertemos para a hora do Brasil.
        ZonedDateTime inicioLocal = inicio.atZone(fuso);
        ZonedDateTime fimLocal = fim.atZone(fuso);

        CompromissoResponseDTO dto = new CompromissoResponseDTO();
        dto.setId(texto(booking.get("uid")));
        dto.setDate(inicioLocal.toLocalDate().toString());      // 2026-11-03
        dto.setStartTime(HORA.format(inicioLocal));             // 14:00
        dto.setEndTime(HORA.format(fimLocal));                  // 14:30
        dto.setTitle(tipo);
        dto.setType(tipo);
        dto.setCompanyId(texto(metadata.get(META_LEAD)));
        dto.setCompanyName(nomeEmpresa);
        dto.setContactName(texto(convidado.get("name")));
        dto.setDetail(texto(campos.get("notes")));
        dto.setTone(definirCor(tipo));
        dto.setStatus(texto(booking.get("status")));
        return dto;
    }

    /** Lê o id da empresa que o LeadWay escreveu no booking. Devolve null se não houver. */
    public Integer lerLeadId(Map<String, Object> booking) {
        Map<String, Object> metadata = comoObjeto(booking.get("metadata"));
        String valor = texto(metadata.get(META_LEAD));
        if (valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Converte um texto de data do Cal.com (ex.: 2026-11-03T15:30:00Z) em Instant. */
    public Instant lerInstante(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(valor.toString()).toInstant();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Cada tipo de compromisso tem uma cor na tela (as mesmas que o front já usava).
    private String definirCor(String tipo) {
        if (tipo.equals("Reunião")) {
            return "purple";
        }
        if (tipo.equals("Visita")) {
            return "blue";
        }
        if (tipo.equals("Follow-up") || tipo.equals("Orçamento")) {
            return "warning";
        }
        return "brand";
    }

    private Map<String, Object> primeiroConvidado(Object attendees) {
        if (attendees instanceof List && !((List<?>) attendees).isEmpty()) {
            return comoObjeto(((List<?>) attendees).get(0));
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> comoObjeto(Object valor) {
        if (valor instanceof Map) {
            return (Map<String, Object>) valor;
        }
        return new HashMap<>();
    }

    // Texto vazio vira null, para a tela não mostrar "" nem "null".
    private String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }
        return valor.toString();
    }
}
