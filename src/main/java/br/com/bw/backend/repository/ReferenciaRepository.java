package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Referencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenciaRepository extends JpaRepository<Referencia, Integer> {
    Page<Referencia> findAll(Pageable pageable);

    List<Referencia> findAll(Sort sort);
}
