package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LeadVazioException extends RuntimeException {
    public LeadVazioException(String message) {
        super(message);
    }
}
