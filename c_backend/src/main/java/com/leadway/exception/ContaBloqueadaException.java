package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ContaBloqueadaException extends RuntimeException {
    public ContaBloqueadaException() {
        super("Conta temporariamente bloqueada. Tente novamente mais tarde.");
    }
}
