package com.leadway.service;

import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final VendedorRepository repository;
    private final LeadRepository leads;
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

    public SessionService(VendedorRepository repository, LeadRepository leads) {
        this.repository = repository;
        this.leads = leads;
    }

    public String issue(Vendedor user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getId());
        return token;
    }

    public Vendedor require(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login necessário");
        Integer id = tokens.get(header.substring(7));
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida");
        Vendedor user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Conta não encontrada"));
        if (!Boolean.TRUE.equals(user.getAtivo()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta inativa");
        return user;
    }

    public void revoke(String header) {
        if (header != null && header.startsWith("Bearer ")) tokens.remove(header.substring(7));
    }

    public String role(Vendedor user) {
        return user.getCargo() == null ? "" : user.getCargo().getNome();
    }

    public Vendedor root(Vendedor user) {
        Vendedor current = user;
        Set<Integer> seen = new HashSet<>();
        while (current.getLider() != null) {
            if (!seen.add(current.getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hierarquia circular");
            current = current.getLider();
        }
        return current;
    }

    public void requireManager(Vendedor user) {
        if (!role(user).equals("ADMINISTRADOR") && !role(user).equals("GERENTE"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão");
    }

    public void sameOrg(Vendedor actor, Vendedor target) {
        if (!root(actor).getId().equals(root(target).getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Organização diferente");
    }

    /**
     * Autorização compartilhada pelos endpoints do calcom que foram portados
     * (detalhe, transferência e anotações). Mantém a regra de carteira/equipe
     * já usada por LeadController no feature/teste-all.
     */
    public Lead requireLeadAccess(Vendedor actor, Integer leadId) {
        Lead lead = leads.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead não encontrado"));
        if (lead.getVendedor() == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lead sem responsável autorizado");
        sameOrg(actor, lead.getVendedor());
        if (role(actor).equals("VENDEDOR") && !actor.getId().equals(lead.getVendedor().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lead fora da carteira");
        if (role(actor).equals("GERENTE") && !actor.getId().equals(lead.getVendedor().getId())
                && (lead.getVendedor().getLider() == null
                || !actor.getId().equals(lead.getVendedor().getLider().getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lead fora da equipe");
        return lead;
    }
}
