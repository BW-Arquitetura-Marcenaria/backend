package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {

    Page<Projeto> findByClienteId(Integer clienteId, Pageable pageable);

    Optional<Projeto> findByIdAndClienteId(Integer id, Integer clienteId);
}