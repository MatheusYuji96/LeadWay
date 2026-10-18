package com.leadway.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClassificacaoNaoValidaException extends RuntimeException {
    public ClassificacaoNaoValidaException(String message) {
        super(message);
    }
}
