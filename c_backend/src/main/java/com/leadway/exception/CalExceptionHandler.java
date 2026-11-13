package com.leadway.exception;

import com.leadway.controller.AgendaController;
import com.leadway.controller.CalIntegracaoController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/*"Plantão de erros" dos controllers da agenda.
Quando um Service lança uma CalException, o Spring procura quem saiba tratá-la e chama o
método abaixo, que monta a resposta HTTP: o status que veio na exceção + um JSON
{ "message": "texto para o usuário" } (é esse "message" que a tela exibe).
Detalhe importante: assignableTypes limita este tratador APENAS aos dois controllers da
agenda. Os demais controllers do projeto continuam respondendo erros exatamente como antes.*/
@RestControllerAdvice(assignableTypes = {AgendaController.class, CalIntegracaoController.class})
public class CalExceptionHandler {

    @ExceptionHandler(CalException.class)
    public ResponseEntity<Map<String, Object>> tratarCalException(CalException e) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(corpo);
    }
}
