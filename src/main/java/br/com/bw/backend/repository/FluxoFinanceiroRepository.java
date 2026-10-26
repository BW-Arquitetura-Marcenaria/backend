package br.com.bw.backend.repository;

import br.com.bw.backend.entity.FluxoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FluxoFinanceiroRepository extends JpaRepository<FluxoFinanceiro, Integer> {
}
