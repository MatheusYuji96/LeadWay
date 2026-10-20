package com.leadway.repository;

import com.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CadastroLeadRepository extends JpaRepository<Lead, Integer> {
}
