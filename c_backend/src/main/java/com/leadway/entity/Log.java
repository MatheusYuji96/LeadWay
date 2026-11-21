package com.leadway.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="log")
public class Log {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(length=45,nullable=false) private String tipo;
 @Column(length=100,nullable=false) private String mensagem;
 private LocalDateTime criadoEm;
 public Integer getId(){return id;} public String getTipo(){return tipo;}
 public void setTipo(String v){tipo=v;} public String getMensagem(){return mensagem;}
 public void setMensagem(String v){mensagem=v;}
 public LocalDateTime getCriadoEm(){return criadoEm;}
 @PrePersist void onCreate(){if(criadoEm==null)criadoEm=LocalDateTime.now();}
}
