package com.leadway.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
public class Interacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // contatos_id

    // leads_id

    @Column(nullable = false, length = 45)
    private String tipo;

    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime criadoEm;

}
