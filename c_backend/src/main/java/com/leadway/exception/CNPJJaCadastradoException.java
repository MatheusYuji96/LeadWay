package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CNPJJaCadastradoException extends RuntimeException {
    public CNPJJaCadastradoException(String message) {
        super(message);
    }
}
