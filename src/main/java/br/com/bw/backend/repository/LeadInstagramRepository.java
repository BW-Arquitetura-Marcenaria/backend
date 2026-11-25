package br.com.bw.backend.repository;

import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LeadInstagramRepository extends JpaRepository<LeadInstagram, Integer> {

    Optional<LeadInstagram> findByInstagramId(String instagramId);

    Optional<LeadInstagram> findByTokenHash(String tokenHash);

    Optional<LeadInstagram> findByUsuarioId(Integer usuarioId);

    Optional<LeadInstagram> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u, l
              FROM Usuario u
              LEFT JOIN LeadInstagram l ON l.usuario = u
             WHERE u.perfil = :perfil
             ORDER BY u.id DESC
            """)
    List<Object[]> findClientesComLead(@Param("perfil") Perfil perfil);
}