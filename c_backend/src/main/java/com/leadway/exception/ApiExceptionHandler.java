package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private Map<String, Object> body(int status, String message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", status);
        data.put("message", message);
        return data;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        String reason = ex.getReason();
        if (reason == null || reason.isBlank()) reason = "Não foi possível concluir a operação.";
        return ResponseEntity.status(ex.getStatusCode()).body(body(status, reason));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + (error.getDefaultMessage() == null ? "valor inválido" : error.getDefaultMessage()))
            .distinct().collect(Collectors.joining("; "));
        if (message.isBlank()) message = "Verifique os campos informados.";
        return ResponseEntity.badRequest().body(body(400, message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(body(409, "A operação viola uma restrição de dados. Verifique se o registro já existe ou possui informações vinculadas."));
    }
}
