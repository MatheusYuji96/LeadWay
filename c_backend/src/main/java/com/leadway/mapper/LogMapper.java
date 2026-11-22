package com.leadway.mapper;

import com.leadway.dto.response.LogResponseDTO;
import com.leadway.entity.Log;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class LogMapper {

    private static final DateTimeFormatter DATA_LONGA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LogResponseDTO toResponse(Log log) {
        LogResponseDTO dto = new LogResponseDTO();
        dto.setId(log.getId() == null ? null : String.valueOf(log.getId()));
        dto.setIcon(icon(log.getTipo()));
        dto.setTitle(titulo(log.getTipo()));
        dto.setDescription(log.getMensagem());
        dto.setTone(tom(log.getTipo()));
        dto.setCreatedAt(tempoRelativo(log.getCriadoEm()));
        // A tabela log não possui coluna de leitura.
        dto.setRead(false);
        return dto;
    }

    private String icon(String tipo) {
        return switch (normalizar(tipo)) {
            case "TRANSFERENCIA" -> "briefcase";
            case "CADASTRO", "LEAD", "LEAD_CADASTRADO" -> "briefcase";
            case "INTERACAO", "ANOTACAO", "ATIVIDADE" -> "clock";
            case "AGENDA", "COMPROMISSO", "REUNIAO" -> "calendar";
            default -> "bell";
        };
    }

    private String titulo(String tipo) {
        return switch (normalizar(tipo)) {
            case "TRANSFERENCIA" -> "Responsável alterado";
            case "SISTEMA" -> "Aviso do sistema";
            case "CADASTRO", "LEAD", "LEAD_CADASTRADO" -> "Novo lead cadastrado";
            case "INTERACAO", "ANOTACAO", "ATIVIDADE" -> "Nova interação registrada";
            case "AGENDA", "COMPROMISSO", "REUNIAO" -> "Compromisso agendado";
            default -> tipo == null || tipo.isBlank()
                    ? "Notificação"
                    : Character.toUpperCase(tipo.charAt(0)) + tipo.substring(1).toLowerCase(Locale.ROOT);
        };
    }

    private String tom(String tipo) {
        return switch (normalizar(tipo)) {
            case "TRANSFERENCIA", "AGENDA", "COMPROMISSO", "REUNIAO" -> "blue";
            case "INTERACAO", "ANOTACAO", "ATIVIDADE" -> "warning";
            case "SISTEMA" -> "brand";
            default -> "brand";
        };
    }

    private String normalizar(String tipo) {
        return tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
    }

    private String tempoRelativo(LocalDateTime criadoEm) {
        if (criadoEm == null) {
            return "";
        }
        LocalDateTime agora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(criadoEm, agora);
        if (minutos < 1) {
            return "Agora";
        }
        if (minutos < 60) {
            return "Há " + minutos + (minutos == 1 ? " minuto" : " minutos");
        }
        long horas = ChronoUnit.HOURS.between(criadoEm, agora);
        if (horas < 24) {
            return "Há " + horas + (horas == 1 ? " hora" : " horas");
        }
        long dias = ChronoUnit.DAYS.between(criadoEm, agora);
        if (dias < 30) {
            return "Há " + dias + (dias == 1 ? " dia" : " dias");
        }
        return criadoEm.format(DATA_LONGA);
    }
}
