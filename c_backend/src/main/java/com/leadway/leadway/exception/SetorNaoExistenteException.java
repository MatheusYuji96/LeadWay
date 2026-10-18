package com.leadway.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SetorNaoExistenteException extends RuntimeException {
    public SetorNaoExistenteException(String message) {
        super(message);
    }
}
