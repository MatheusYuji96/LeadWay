package com.leadway.exception;

import org.springframework.http.HttpStatus;

public class CalException extends RuntimeException {

    private final HttpStatus status;

    public CalException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ----- Atalhos para os erros que se repetem (cada um já escolhe o status certo) -----

    /** 409: o vendedor ainda não colou a API key do Cal.com (ou desconectou). */
    public static CalException naoConectado() {
        return new CalException(HttpStatus.CONFLICT, "Conecte sua conta do Cal.com para usar a agenda.");
    }

    /** 400: o usuário mandou algo inválido (empresa inexistente, horário no passado...). */
    public static CalException invalido(String mensagem) {
        return new CalException(HttpStatus.BAD_REQUEST, mensagem);
    }

    /** 404: não encontramos o que foi pedido (ex.: vendedor inexistente). */
    public static CalException naoEncontrado(String mensagem) {
        return new CalException(HttpStatus.NOT_FOUND, mensagem);
    }

    /** 422: o Cal.com recusou o pedido (API key inválida, horário já ocupado...). */
    public static CalException recusado(String mensagem) {
        return new CalException(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
    }

    /** 502: o Cal.com está fora do ar, lento ou deu erro interno (culpa dele, não nossa). */
    public static CalException indisponivel() {
        return new CalException(HttpStatus.BAD_GATEWAY,
                "Não foi possível falar com o Cal.com agora. Tente novamente em instantes.");
    }
}
