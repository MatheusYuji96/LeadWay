package com.leadway.repository;

import com.leadway.entity.StatusTarefa;
import com.leadway.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    Integer countByLead_Vendedor_IdAndStatusAndDataPrazoGreaterThanEqualAndDataPrazoLessThan(
            Integer idVendedor,
            StatusTarefa status,
            LocalDateTime inicio,
            LocalDateTime fim
    );


    List<Tarefa> findTop3ByLead_Vendedor_IdAndStatusAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
            Integer idVendedor,
            StatusTarefa status,
            LocalDateTime horarioAtual
    );

    Tarefa findFirstByLead_IdAndStatusAndDataPrazoGreaterThanEqualOrderByDataPrazoAsc(
            Integer id,
            StatusTarefa statusTarefa,
            LocalDateTime horarioAtual
    );
}
