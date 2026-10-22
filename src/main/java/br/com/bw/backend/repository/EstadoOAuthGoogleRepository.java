package br.com.bw.backend.repository;

import br.com.bw.backend.entity.EstadoOAuthGoogle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoOAuthGoogleRepository extends JpaRepository<EstadoOAuthGoogleRepository, Integer> {
    Optional<EstadoOAuthGoogle> findByHashEstado(String hashEstado);
}
