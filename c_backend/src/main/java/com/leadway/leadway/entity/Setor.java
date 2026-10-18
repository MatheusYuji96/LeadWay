package com.leadway.leadway.entity;

import jakarta.persistence.*;

@Entity
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String nome;

<<<<<<< HEAD:c_backend/src/main/java/com/leadway/leadway/entity/Setor.java
    public Setor() {
=======
    protected Setor() {
>>>>>>> origin/development:c_backend/leadway/src/main/java/com/leadway/entity/Setor.java
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
