package com.leadway.controller;

import com.leadway.entity.*;
import com.leadway.repository.*;
import com.leadway.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name="Cadências",description="Acompanhamento de leads")
@RestController
@RequestMapping("/cadencias")
public class CadenciaController {
 private final CadenciaRepository cadencias;
 private final LeadRepository leads;
 private final ContatoRepository contatos;
 private final SessionService sessions;
 public CadenciaController(CadenciaRepository cadencias,LeadRepository leads,ContatoRepository contatos,SessionService sessions){this.cadencias=cadencias;this.leads=leads;this.contatos=contatos;this.sessions=sessions;}
 public record CadenciaDados(Integer leadId,Integer contatoId,String frequencia,Integer intervaloDias,LocalDateTime dataInicio,LocalDateTime dataFim,Integer responsavelId,String status){}
 public record CadenciaResposta(Integer id,Integer leadId,Integer contatoId,String frequencia,Integer intervaloDias,LocalDateTime dataInicio,LocalDateTime dataFim,Integer responsavelId,String status,LocalDateTime criadoEm){}
 private CadenciaResposta output(Cadencia c){return new CadenciaResposta(c.getId(),c.getLead().getId(),c.getContato()==null?null:c.getContato().getId(),c.getFrequencia(),c.getIntervaloDias(),c.getDataInicio(),c.getDataFim(),c.getResponsavel()==null?null:c.getResponsavel().getId(),c.getStatus(),c.getCriadoEm());}
 private void allowed(Vendedor actor,Lead lead){
  Vendedor owner=lead.getVendedor();
  if(owner==null)throw new ResponseStatusException(HttpStatus.CONFLICT,"Lead sem responsável");
  // Administradores podem gerenciar cadências de qualquer lead.
  // A modelagem atual não possui identificador de organização.
  if(sessions.role(actor).equals("ADMINISTRADOR"))return;
  sessions.sameOrg(actor,owner);
  if(sessions.role(actor).equals("VENDEDOR")&&!actor.getId().equals(owner.getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Lead de outro vendedor");
  if(sessions.role(actor).equals("GERENTE")&&!actor.getId().equals(owner.getId())&&!Objects.equals(owner.getLider()==null?null:owner.getLider().getId(),actor.getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Lead fora da equipe");
 }
 @Operation(summary="Lista cadências acessíveis ao usuário")
 @GetMapping public List<CadenciaResposta> list(@RequestHeader(value="Authorization",required=false) String token){Vendedor actor=sessions.require(token);return cadencias.findAll().stream().filter(c->{try{allowed(actor,c.getLead());return true;}catch(ResponseStatusException ex){return false;}}).map(this::output).toList();}
 private void fill(Cadencia c,CadenciaDados d,Vendedor actor){
  if(d.leadId()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Lead obrigatório");
  Lead lead=leads.findById(d.leadId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Lead não encontrado"));allowed(actor,lead);
  if(!Set.of("DIARIA","SEMANAL","QUINZENAL","MENSAL","PERSONALIZADA").contains(d.frequencia()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Frequência inválida");
  if("PERSONALIZADA".equals(d.frequencia())&&(d.intervaloDias()==null||d.intervaloDias()<1))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Intervalo inválido");
  if(d.status()!=null&&!Set.of("ATIVA","PAUSADA","CONCLUIDA","CANCELADA").contains(d.status()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status inválido");
  Contato contato=d.contatoId()==null?null:contatos.findById(d.contatoId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contato não encontrado"));
  if(contato!=null&&(contato.getLead()==null||!Objects.equals(contato.getLead().getId(),lead.getId())))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Contato não pertence ao lead");
  if(d.dataFim()!=null && d.dataInicio()!=null && d.dataFim().isBefore(d.dataInicio()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data final anterior à inicial");
  if(d.responsavelId()!=null && !d.responsavelId().equals(lead.getVendedor().getId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Responsável deve ser o responsável atual do lead");
  c.setDataFim(d.dataFim());
  c.setLead(lead);c.setContato(contato);c.setResponsavel(lead.getVendedor());c.setFrequencia(d.frequencia());c.setIntervaloDias(d.intervaloDias());c.setDataInicio(d.dataInicio()==null?LocalDateTime.now():d.dataInicio());c.setStatus(d.status()==null?"ATIVA":d.status());
 }
 @Operation(summary="Cria uma cadência")
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public CadenciaResposta create(@RequestHeader(value="Authorization",required=false) String token,@RequestBody CadenciaDados data){Vendedor actor=sessions.require(token);Cadencia c=new Cadencia();fill(c,data,actor);return output(cadencias.save(c));}
 @Operation(summary="Edita uma cadência")
 @PatchMapping("/{id}") public CadenciaResposta update(@RequestHeader(value="Authorization",required=false) String token,@PathVariable Integer id,@RequestBody CadenciaDados data){Vendedor actor=sessions.require(token);Cadencia c=cadencias.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cadência não encontrada"));allowed(actor,c.getLead());fill(c,data,actor);return output(cadencias.save(c));}
}
