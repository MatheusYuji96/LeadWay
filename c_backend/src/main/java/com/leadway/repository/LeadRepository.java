package com.leadway.repository;

import com.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Integer> {

    @Query("select l from Lead l left join l.vendedor v left join l.etapa e where " +
            "(:termo is null or :termo = '' or " +
            "lower(l.nomeFantasia) like lower(concat('%', :termo, '%')) or " +
            "l.cnpj like concat('%', :termo, '%') or " +
            "lower(v.nome) like lower(concat('%', :termo, '%'))) and " +
            "(:vendedorId is null or v.id = :vendedorId) and " +
    "(:fase is null or :fase = '' or e.nome = :fase)")
    List<Lead> findByFiltros(@Param("termo") String termo, @Param("vendedorId") Integer vendedorId, @Param("fase") String fase);
    Integer countByVendedor_IdAndAtivoTrueAndDataPrimeiraVisitaIsNull(Integer vendedorId);

    Boolean existsByEmailIgnoreCase(String email);
    Integer countByVendedor_IdAndAtivoTrue(Integer idVendedor);

    Boolean existsByCNPJ(String cnpj);
    List<Lead> findByVendedor_IdAndAtivoTrue(Integer vendedorId);
}
