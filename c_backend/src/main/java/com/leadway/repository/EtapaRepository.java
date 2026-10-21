package com.leadway.repository;

import com.leadway.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    Optional<Etapa> findFirstByOrderByIdAsc();
}
