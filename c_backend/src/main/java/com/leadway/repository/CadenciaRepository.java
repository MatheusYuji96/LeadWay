package com.leadway.repository;

import com.leadway.entity.Cadencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CadenciaRepository extends JpaRepository<Cadencia, Integer> {

    /** Mantido do feature/calcom: alimenta CadenciaMapper e o detalhe do lead. */
    List<Cadencia> findByLead_IdOrderByCriadoEmDesc(Integer leadId);
}
