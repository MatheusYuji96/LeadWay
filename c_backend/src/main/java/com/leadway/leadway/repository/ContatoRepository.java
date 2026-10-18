package com.leadway.leadway.repository;

import com.leadway.leadway.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {
    Boolean existsByEmailIgnoreCase (String email);
}
