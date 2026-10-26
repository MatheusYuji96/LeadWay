package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EtapaNaoRegistradaException extends RuntimeException {
    public EtapaNaoRegistradaException(String message) {
        super(message);
    }
}
