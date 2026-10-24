package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.entity.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    List<Agendamento> findAllByOrderByCriadoEmDesc();

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Agendamento a
           SET a.status = :novoStatus,
               a.observacaoAdmin = :observacao
         WHERE a.id = :id
           AND a.status = :statusEsperado
        """)
    int atualizarStatusSeStatusAtualFor(
            @Param("id") Integer id,
            @Param("statusEsperado") StatusAgendamento statusEsperado,
            @Param("novoStatus") StatusAgendamento novoStatus,
            @Param("observacao") String observacao
    );
}
