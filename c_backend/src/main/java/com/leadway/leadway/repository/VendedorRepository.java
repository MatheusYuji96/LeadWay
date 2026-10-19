package com.leadway.leadway.repository;

import com.leadway.leadway.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
    Boolean existsByEmailIgnoreCase(String email);
}
