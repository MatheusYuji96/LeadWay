package com.leadway.service;
import com.leadway.entity.Vendedor;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
@Service
public class SessionService {
 private final VendedorRepository repository;
 private final Map<String,Integer> tokens=new ConcurrentHashMap<>();
 public SessionService(VendedorRepository repository){this.repository=repository;}
 public String issue(Vendedor user){String token=UUID.randomUUID().toString();tokens.put(token,user.getId());return token;}
 public Vendedor require(String header){
  if(header==null || !header.startsWith("Bearer "))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login necessário");
  Integer id=tokens.get(header.substring(7));
  if(id==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sessão inválida");
  Vendedor user=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Conta não encontrada"));
  if(!Boolean.TRUE.equals(user.getAtivo()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Conta inativa");
  return user;
 }
 public void revoke(String header){if(header!=null && header.startsWith("Bearer "))tokens.remove(header.substring(7));}
 public String role(Vendedor user){return user.getCargo()==null?"":user.getCargo().getNome();}
 public Vendedor root(Vendedor user){
  Vendedor current=user;java.util.Set<Integer> seen=new java.util.HashSet<>();
  while(current.getLider()!=null){if(!seen.add(current.getId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Hierarquia circular");current=current.getLider();}
  return current;
 }
 public void requireManager(Vendedor user){if(!role(user).equals("ADMINISTRADOR")&&!role(user).equals("GERENTE"))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Sem permissão");}
 public void sameOrg(Vendedor actor,Vendedor target){if(!root(actor).getId().equals(root(target).getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organização diferente");}
}
