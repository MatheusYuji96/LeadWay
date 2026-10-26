package com.leadway.repository;

import com.leadway.entity.LeadEtapaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadEtapaHistoricoRepository extends JpaRepository<LeadEtapaHistorico, Integer> {

    @Query(value = """
     SELECT
          DATE(h.criado_em),
          h.etapa_id,
          COUNT(*)
      FROM lead_etapa_historico h
      INNER JOIN leads l ON l.id = h.lead_id
      WHERE l.vendedor_id = :vendedorId
        AND h.criado_em >= :inicio
        AND h.criado_em < :fim
        AND h.etapa_id IN (2, 3, 4)
      GROUP BY DATE(h.criado_em), h.etapa_id
        """, nativeQuery = true)
    List<Object[]> contarAtividadesPorDia(
            @Param("vendedorId") Integer vendedorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
