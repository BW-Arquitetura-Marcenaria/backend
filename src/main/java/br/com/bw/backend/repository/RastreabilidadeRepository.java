package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Rastreabilidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RastreabilidadeRepository extends JpaRepository<Rastreabilidade, Integer> {

    @Query(value = "select r from Rastreabilidade r join fetch r.projeto",
            countQuery = "select count(r) from Rastreabilidade r")
    Page<Rastreabilidade> findAllComProjeto(Pageable pageable);

    @Query(value = "select r from Rastreabilidade r join fetch r.projeto where r.projeto.id = :projetoId",
            countQuery = "select count(r) from Rastreabilidade r where r.projeto.id = :projetoId")
    Page<Rastreabilidade> findByProjetoId(@Param("projetoId") Integer projetoId, Pageable pageable);
}
