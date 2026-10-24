package com.leadway.repository;

import com.leadway.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    Integer countByLead_Vendedor_IdAndStatus_AbertaAndDataPrazoGreaterThanEqualAndDataPrazoLessThan(
            Integer idVendedor,
            LocalDateTime inicio,
            LocalDateTime fim
    );


    List<Tarefa> findTop3ByLead_Vendedor_IdAndStatus_AbertaAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
            Integer idVendedor,
            LocalDateTime horarioAtual
    );
}
