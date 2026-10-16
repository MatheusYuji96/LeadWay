package com.leadway.repository;

import com.leadway.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CadastroVendedorRepository extends JpaRepository<Vendedor, Integer> {
}
