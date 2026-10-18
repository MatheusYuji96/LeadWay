package com.leadway.leadway.entity;

import jakarta.persistence.*;

@Entity
public class Etapa {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(columnDefinition = "TINYINT")
    private Integer ordem;

    public Etapa() {
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
