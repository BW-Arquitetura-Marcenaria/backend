package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    Optional<Endereco> findByEmpresaId(Integer empresaId);
}
