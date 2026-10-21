package com.leadway.repository;

import com.leadway.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {
    Boolean existsByEmailIgnoreCase (String email);
}
