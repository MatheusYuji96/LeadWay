package com.leadway.leadway.repository;

import com.leadway.leadway.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    Etapa findFirstByIdAsc();
}
