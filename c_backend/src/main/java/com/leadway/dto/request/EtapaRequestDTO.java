package com.leadway.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;

@Component
public class EtapaRequestDTO {

    @NotBlank
    private String nome;

    private Integer ordem;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
