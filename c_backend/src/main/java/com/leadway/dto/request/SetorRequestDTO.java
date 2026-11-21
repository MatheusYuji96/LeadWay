package com.leadway.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;

@Component
public class SetorRequestDTO {

    @NotBlank
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
