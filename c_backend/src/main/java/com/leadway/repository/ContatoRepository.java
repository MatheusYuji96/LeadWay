package com.leadway.repository;

import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {

    @Query("select c from Contato c left join c.lead l where " +
            "(:termo is null or :termo = '' or " +
            "lower(c.nome) like lower(concat('%', :termo, '%')) or " +
            "lower(c.cargo) like lower(concat('%', :termo, '%'))) and " +
            "(:leadId is null or l.id = :leadId)")
    List<Contato> findByFiltros(@Param("termo") String termo, @Param("leadId") Integer leadId);

    Boolean existsByEmailIgnoreCase(String email);
}
