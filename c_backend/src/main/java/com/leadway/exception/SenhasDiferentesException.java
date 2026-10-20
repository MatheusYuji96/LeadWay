package com.leadway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SenhasDiferentesException extends RuntimeException {

    public SenhasDiferentesException() {
        super("A senha e a confirmação de senha não coincidem.");
    }
}
