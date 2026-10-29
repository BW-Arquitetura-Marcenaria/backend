package br.com.bw.backend.repository;

import br.com.bw.backend.entity.ChatbotLog;
import br.com.bw.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Integer> {

    List<ChatbotLog> findByClienteIdOrderByDataInteracaoAscIdAsc(Integer clienteId);

    @Modifying
    @Query("""
            UPDATE ChatbotLog c
               SET c.cliente = :usuario
             WHERE c.instagramId = :instagramId
               AND c.cliente IS NULL
            """)
    int vincularUsuario(@Param("instagramId") String instagramId,
                        @Param("usuario") Usuario usuario);
}
