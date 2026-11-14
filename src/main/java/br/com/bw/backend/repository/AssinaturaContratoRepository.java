package br.com.bw.backend.repository;

import br.com.bw.backend.entity.AssinaturaContrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssinaturaContratoRepository extends JpaRepository<AssinaturaContrato, Integer> {

    List<AssinaturaContrato> findByProjetoId(Integer projetoId);

    Optional<AssinaturaContrato> findByIdAndProjetoId(Integer id, Integer projetoId);

    Optional<AssinaturaContrato> findByEnvelopeId(String envelopeId);

    Optional<AssinaturaContrato> findByDocumentId(String documentId);
}
