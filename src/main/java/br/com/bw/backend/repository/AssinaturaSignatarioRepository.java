package br.com.bw.backend.repository;

import br.com.bw.backend.entity.AssinaturaSignatario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssinaturaSignatarioRepository extends JpaRepository<AssinaturaSignatario, Integer> {

    Optional<AssinaturaSignatario> findBySignerId(String signerId);
}
