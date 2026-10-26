package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContatoNaoExistenteException extends RuntimeException {
    public ContatoNaoExistenteException(String message) {
        super(message);
    }
}
