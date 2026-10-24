package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.entity.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    List<Agendamento> findAllByOrderByCriadoEmDesc();

    Optional<Agendamento> findByGoogleEventId(String googleEventId);

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

    @Query("""
        SELECT COUNT(a) > 0
          FROM Agendamento a
         WHERE a.status IN :statusQueOcupamAgenda
           AND a.inicio < :fim
           AND a.fim > :inicio
        """)
    boolean existeConflitoDeHorario(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("statusQueOcupamAgenda") List<StatusAgendamento> statusQueOcupamAgenda
    );
}
