package br.com.bw.backend.repository;

import br.com.bw.backend.entity.MensagemChat;
import br.com.bw.backend.entity.enums.RemetenteChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MensagemChatRepository extends JpaRepository<MensagemChat, Integer> {

    List<MensagemChat> findByClienteIdAndIdGreaterThanOrderByIdAsc(Integer clienteId, Integer depois);

    @Query("""
            SELECT m
              FROM MensagemChat m
              JOIN FETCH m.cliente
             WHERE m.id IN (SELECT MAX(m2.id)
                              FROM MensagemChat m2
                             GROUP BY m2.cliente.id)
             ORDER BY m.id DESC
            """)
    List<MensagemChat> findUltimaMensagemDeCadaConversa();


    long countByClienteIdAndRemetenteAndLidaEmIsNull(Integer clienteId, RemetenteChat remetente);

    long countByRemetenteAndLidaEmIsNull(RemetenteChat remetente);

    boolean existsByClienteIdAndRemetenteAndLidaEmIsNull(Integer clienteId, RemetenteChat remetente);

    @Query("""
            SELECT m.cliente.id AS clienteId, COUNT(m) AS total
              FROM MensagemChat m
             WHERE m.remetente = :remetente
               AND m.lidaEm IS NULL
             GROUP BY m.cliente.id
            """)
    List<ContagemNaoLidas> contarNaoLidasPorConversa(@Param("remetente") RemetenteChat remetente);

    @Modifying
    @Query("""
            UPDATE MensagemChat m
               SET m.lidaEm = :agora
             WHERE m.cliente.id = :clienteId
               AND m.remetente = :remetente
               AND m.lidaEm IS NULL
            """)
    int marcarComoLidas(@Param("clienteId") Integer clienteId,
                        @Param("remetente") RemetenteChat remetente,
                        @Param("agora") LocalDateTime agora);

    interface ContagemNaoLidas {
        Integer getClienteId();

        Long getTotal();
    }
}