package com.leadway.repository;

import com.leadway.entity.Interacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteracaoRepository extends JpaRepository<Interacao, Integer> {
    List<Interacao> findByLead_Id(Integer leadId);
}
