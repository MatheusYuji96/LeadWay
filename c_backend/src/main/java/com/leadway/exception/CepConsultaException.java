package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class CepConsultaException extends RuntimeException {
    public CepConsultaException(String message) {
        super(message);
    }
    public CepConsultaException(String message, Throwable cause) {
        super(message, cause);
    }
}
