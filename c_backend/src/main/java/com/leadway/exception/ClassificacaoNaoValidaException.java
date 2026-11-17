package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClassificacaoNaoValidaException extends RuntimeException {
    public ClassificacaoNaoValidaException(String message) {
        super(message);
    }
}
