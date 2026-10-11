package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // setor_id

    // endereco_id

    // vendedores_id

    // etapas_id

    @Column(columnDefinition = "CHAR(14)", nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false, length = 190)
    private String razaoSocial;

    @Column(nullable = false, length = 190)
    private String nomeFantasia;

    @Column(nullable = false, length = 255,unique = true)
    private String email;

    @Column(columnDefinition = "CHAR(11)", nullable = false)
    private String telefone;

    @Column(nullable = false, length = 30)
    private String prioridade;

    @Column(nullable = false, length = 30)
    private String potencial;

    @Column(nullable = false, length = 20)
    private String farol;

    private LocalDateTime dataPrimeiroContato;

    private LocalDateTime dataPrimeiraVisita;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(columnDefinition = "TEXT")
    private String motivoPerda;

    @Column(nullable = false)
    private Boolean ativo = true;
}
