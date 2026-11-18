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

    Boolean existsBycnpj(String cnpj);

    List<Lead> findByVendedor_IdAndAtivoTrue(Integer idVendedor);

    @Query("select count(l) from Lead l " + "where l.ativo =  true " + "and l.vendedor.ativo = true " + "and l.vendedor.lider = :liderId")
    Long contarCarteiraPorLider(@Param("liderId") Integer liderId);

    @Query("select count(l) from Lead l " + "where l.ativo = true " + "and l.vendedor.ativo = true " + "and l.vendedor.lider.id = :liderId " + "and l.etapa.ordem < :ordemEtapaFechada " + "and l.criadoEm < :limite " + "and not exists (select i.id from Interacao i " + "where i.lead = l " + "and coalesce(i.dataHora, i.criadoEm) >= :limite)")
    Long contarParadosPorLider(@Param("liderId") Integer liderId, @Param("limite") LocalDateTime limite, @Param("ordemEtapaFechada") Integer ordemEtapaFechada);

    Long countByVendedor_IdAndAtivoTrueAndEtapa_Ordem(Integer vendedorId, Integer ordemEtapa);

    @Query("select count(l) from Lead l " + "where l.ativo = true " + "and l.vendedor.id = :vendedorId " + "and l.etapa.ordem < :ordemEtapaFechada " + "and l.criadoEm < :limite " + "and not exists (select i.id from Interacao i " + "where i.lead = l " + "and coalesce(i.dataHora, i.criadoEm) >= :limite)")
    Long contarParadosPorVendedor (@Param("vendedorId") Integer vendedorId, @Param("limite") LocalDateTime limite, @Param("ordemEtapaFechada") Integer ordemEtapaFechada);
}
