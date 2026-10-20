package com.leadway.repository;

import com.leadway.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {

    Boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    Optional<Vendedor> findByEmail(String email);
}

