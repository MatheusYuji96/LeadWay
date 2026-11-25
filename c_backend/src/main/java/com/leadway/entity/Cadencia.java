package com.leadway.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="cadencia")
public class Cadencia {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne @JoinColumn(name="contato_id") private Contato contato;
 @ManyToOne @JoinColumn(name="lead_id",nullable=false) private Lead lead;
 @ManyToOne @JoinColumn(name="responsavel_id") private Vendedor responsavel;
 @Column(nullable=false) private String frequencia;
 @Column(name="intervalo_dias") private Integer intervaloDias;
 private LocalDateTime dataInicio;
 private LocalDateTime dataFim;
 @Column(nullable=false) private String status;
 private LocalDateTime criadoEm;
 private LocalDateTime atualizadoEm;
 @PrePersist
 public void antesDeInserir() {
  LocalDateTime agora = LocalDateTime.now();
  if (criadoEm == null) criadoEm = agora;
  atualizadoEm = agora;
 }
 @PreUpdate
 public void antesDeAtualizar() {
  atualizadoEm = LocalDateTime.now();
 }
 public Integer getId(){return id;}
 public LocalDateTime getCriadoEm(){return criadoEm;}
 public Lead getLead(){return lead;} public void setLead(Lead v){lead=v;}
 public Contato getContato(){return contato;} public void setContato(Contato v){contato=v;}
 public Vendedor getResponsavel(){return responsavel;} public void setResponsavel(Vendedor v){responsavel=v;}
 public String getFrequencia(){return frequencia;} public void setFrequencia(String v){frequencia=v;}
 public Integer getIntervaloDias(){return intervaloDias;} public void setIntervaloDias(Integer v){intervaloDias=v;}
 public LocalDateTime getDataInicio(){return dataInicio;} public void setDataInicio(LocalDateTime v){dataInicio=v;}
 public LocalDateTime getDataFim(){return dataFim;} public void setDataFim(LocalDateTime v){dataFim=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
