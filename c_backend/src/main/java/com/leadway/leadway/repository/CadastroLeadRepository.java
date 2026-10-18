package com.leadway.leadway.repository;

import com.leadway.leadway.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CadastroLeadRepository extends JpaRepository<Lead, Integer> {
}
