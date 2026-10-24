package com.leadway.repository;

import com.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CadastroLeadRepository extends JpaRepository<Lead, Integer> {

    @Query("select l from Lead l left join l.vendedor v where " +
            "(:termo is null or :termo = '' or " +
            "lower(l.nomeFantasia) like lower(concat('%', :termo, '%')) or " +
            "l.cnpj like concat('%', :termo, '%') or " +
            "lower(v.nome) like lower(concat('%', :termo, '%'))) and " +
            "(:vendedorId is null or v.id = :vendedorId) and " +
            "(:cadencia is null or :cadencia = '' or l.cadencia = :cadencia)")
    List<Lead> findByFiltros(@Param("termo") String termo, @Param("vendedorId") Long vendedorId, @Param("cadencia") String cadencia);
    
    Integer countByVendedor_IdAndAtivoTrueAndDataPrimeiraVisitaIsNull(Integer vendedorId);

    Integer countByVendedor_IdAndAtivoTrue(Integer idVendedor);
}
