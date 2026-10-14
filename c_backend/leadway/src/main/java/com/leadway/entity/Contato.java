package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // leads_id

    @Column(nullable = false, length = 190)
    private String nome;

    @Column(nullable = false, length = 45)
    private String cargo;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(length = 11)
    private String telefone;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String whatsapp;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PADRAO', 'INFLUENCIADOR', 'DECISOR')")
    private ClassificacaoEnum classificacao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(nullable = false)
    private Boolean ativo = true;

}
