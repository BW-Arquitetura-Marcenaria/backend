package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceiroRepository extends JpaRepository<Financeiro, Integer> {
}
