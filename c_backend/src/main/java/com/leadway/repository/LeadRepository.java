package com.leadway.repository;

import com.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Integer> {

    Integer countByVendedor_IdAndAtivoTrueAndDataPrimeiraVisitaIsNull(Integer vendedorId);

    Integer countByVendedor_IdAndAtivoTrue(Integer idVendedor);

    List<Lead> findByVendedor_IdAndAtivoTrue(Integer vendedorId);
}
