package com.leadway.entity;

import jakarta.persistence.*;

@Entity
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // lider_id

    @Column(nullable = false, length = 30)
    private String codVendedor;

    @Column(nullable = false, length = 190)
    private String nome;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String senha;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

}
