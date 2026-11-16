package com.leadway.repository;

import com.leadway.entity.Interacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InteracaoRepository extends JpaRepository<Interacao, Integer> {
    List<Interacao> findByLead_Id(Integer leadId);

    @Query("select count(i) from Interacao i " + "where i.lead.vendedor.lider.id = :liderId " + "and i.lead.vendedor.ativo = true " + "and coalesce(i.dataHora, i.criadoEm) >= :inicio " + "and coalesce(i.dataHora, i.criadoEm) < :fim")
    Long contarPorLiderNoPeriodo(@Param("liderId") Integer liderId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("select count(i) from Interacao i " + "where i.lead.vendedor.id = :vendedorId " + "and coalesce(i.dataHora, i.criadoEm) >= :inicio " + "and coalesce(i.dataHora, i.criadoEm) < :fim")
    Long contarVendedorNoPeriodo(@Param("vendedorId") Integer vendedorId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
