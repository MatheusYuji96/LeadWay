package com.leadway.controller;

import com.leadway.dto.response.LogResponseDTO;
import com.leadway.dto.response.NotificacaoResponseDTO;
import com.leadway.entity.Log;
import com.leadway.mapper.LogMapper;
import com.leadway.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({ "/notifications", "/notificacoes" })
public class NotificacaoController {

    private final LogService logService;
    private final LogMapper logMapper;

    public NotificacaoController(LogService logService, LogMapper logMapper) {
        this.logService = logService;
        this.logMapper = logMapper;
    }

    @GetMapping
    public ResponseEntity<NotificacaoResponseDTO> listarLogs() {
        List<LogResponseDTO> items = logService.buscar().stream()
                .map(logMapper::toResponse)
                .toList();
        long unread = items.stream()
                .filter(item -> Boolean.FALSE.equals(item.getRead()))
                .count();

        return ResponseEntity.status(200).body(new NotificacaoResponseDTO(items, (int) unread));
    }
}
