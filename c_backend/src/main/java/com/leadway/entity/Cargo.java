package com.leadway.entity;
import jakarta.persistence.*;
@Entity @Table(name="cargo")
public class Cargo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false,unique=true,length=90) private String nome;
 @Column(length=145) private String descricao;
 public Integer getId(){return id;} public String getNome(){return nome;}
 public void setNome(String nome){this.nome=nome;}
 public String getDescricao(){return descricao;} public void setDescricao(String descricao){this.descricao=descricao;}
}
