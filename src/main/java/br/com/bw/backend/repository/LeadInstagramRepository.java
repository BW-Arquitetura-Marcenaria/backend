package br.com.bw.backend.repository;

import br.com.bw.backend.entity.LeadInstagram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadInstagramRepository extends JpaRepository<LeadInstagram, Integer> {
    Optional<LeadInstagram> findByInstagramId(String instagramId);

    Optional<LeadInstagram> findByTokenHash(String tokenHash);
}
