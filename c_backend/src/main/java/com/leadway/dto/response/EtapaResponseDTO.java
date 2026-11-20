package com.leadway.dto.response;

import org.springframework.stereotype.Component;

@Component
public class EtapaResponseDTO {

    private Integer id;
    private String nome;
    private Integer ordem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
