package com.leadway.entity;

import jakarta.persistence.*;

@Entity
public class Etapa {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(length = 45)
    private String ordem;

    @Column(length = 45)
    private String cadencia_dia;
}
