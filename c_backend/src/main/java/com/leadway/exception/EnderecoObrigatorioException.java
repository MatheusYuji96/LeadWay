package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnderecoObrigatorioException extends RuntimeException {
    public EnderecoObrigatorioException(String message) {
        super(message);
    }
}
