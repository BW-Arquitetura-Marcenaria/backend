package br.com.bw.backend.repository;

import br.com.bw.backend.entity.EstadoOAuthGoogle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EstadoOAuthGoogleRepository extends JpaRepository<EstadoOAuthGoogle, Integer> {
    Optional<EstadoOAuthGoogle> findByHashEstado(String hashEstado);

    @Modifying
    @Query("""
        UPDATE EstadoOAuthGoogle e
        SET e.dataConsumo = :dataConsumo
        WHERE e.hashEstado = :hashEstado
          AND e.hashVinculoNavegador = :hashVinculoNavegador
          AND e.dataExpiracao > :agora
          AND e.dataConsumo IS NULL
        """)
    int consumirSeValido(
            @Param("hashEstado") String hashEstado,
            @Param("hashVinculoNavegador") String hashVinculoNavegador,
            @Param("agora") LocalDateTime agora,
            @Param("dataConsumo") LocalDateTime dataConsumo
    );
}
