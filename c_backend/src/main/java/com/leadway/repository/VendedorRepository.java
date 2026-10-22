package com.leadway.repository;

import com.leadway.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {

@Query("select v from Vendedor v where " + "(:termo is null or :termo = '' or " + "v.codVendedor like lower(concat('%', :termo, '%')) or " + "lower(v.nome) like lower(concat('%', :termo, '%')))")
    List<Vendedor> findByFiltros(@Param("termo") String termo);

    Boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    Optional<Vendedor> findByEmail(String email);
}
