package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Rastreabilidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RastreabilidadeRepository extends JpaRepository<Rastreabilidade, Integer> {

    @Query(value = "select r from Rastreabilidade r join fetch r.projeto",
            countQuery = "select count(r) from Rastreabilidade r")
    Page<Rastreabilidade> findAllComProjeto(Pageable pageable);
}
