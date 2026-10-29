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

    @Query("""
            SELECT l
              FROM LeadInstagram l
              JOIN FETCH l.usuario u
             WHERE u.perfil = :perfil
               AND u.etapaFunil IN :etapas
             ORDER BY l.criadoEm DESC
            """)
    List<LeadInstagram> findParaFunil(@Param("perfil") Perfil perfil,
                                      @Param("etapas") Collection<EtapaFunil> etapas);
}
