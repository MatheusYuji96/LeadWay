package com.leadway.repository;

import com.leadway.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    Etapa findFirstByIdAsc();
}
