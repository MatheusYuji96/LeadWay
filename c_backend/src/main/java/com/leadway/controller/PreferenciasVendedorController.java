package com.leadway.controller;

import com.leadway.entity.PreferenciasVendedor;
import com.leadway.repository.PreferenciasVendedorRepository;
import com.leadway.repository.VendedorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendedores/{vendedorId}/preferencias")
public class PreferenciasVendedorController {
    private final PreferenciasVendedorRepository repository;
    private final VendedorRepository vendedores;
    public PreferenciasVendedorController(PreferenciasVendedorRepository repository, VendedorRepository vendedores) {
        this.repository = repository;
        this.vendedores = vendedores;
    }
    @GetMapping
    public ResponseEntity<PreferenciasVendedor> consultar(@PathVariable Integer vendedorId) {
        if (!vendedores.existsById(vendedorId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(repository.findById(vendedorId).orElseGet(() -> {
            PreferenciasVendedor novo = new PreferenciasVendedor(); novo.setVendedorId(vendedorId); return novo;
        }));
    }
    @PutMapping
    public ResponseEntity<PreferenciasVendedor> salvar(@PathVariable Integer vendedorId, @RequestBody PreferenciasVendedor payload) {
        if (!vendedores.existsById(vendedorId)) return ResponseEntity.notFound().build();
        PreferenciasVendedor preferencias = repository.findById(vendedorId).orElseGet(() -> {
            PreferenciasVendedor novo = new PreferenciasVendedor(); novo.setVendedorId(vendedorId); return novo;
        });
        if (payload.getNewLeads() != null) preferencias.setNewLeads(payload.getNewLeads());
        if (payload.getAppointmentReminders() != null) preferencias.setAppointmentReminders(payload.getAppointmentReminders());
        if (payload.getLateCadenceAlerts() != null) preferencias.setLateCadenceAlerts(payload.getLateCadenceAlerts());
        if (payload.getWeeklySummaryEmail() != null) preferencias.setWeeklySummaryEmail(payload.getWeeklySummaryEmail());
        return ResponseEntity.ok(repository.save(preferencias));
    }
}
