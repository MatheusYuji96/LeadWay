package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LeadDiferenteException extends RuntimeException {
    public LeadDiferenteException(String message) {
        super(message);
    }
}
