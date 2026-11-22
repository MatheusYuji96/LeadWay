package com.leadway.repository;

import com.leadway.entity.Cadencia;
import com.leadway.entity.StatusCadencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CadenciaRepository extends JpaRepository<Cadencia, Integer> {
    Optional<Cadencia> findByLead_IdAndStatus(Integer leadId, StatusCadencia status);
}
