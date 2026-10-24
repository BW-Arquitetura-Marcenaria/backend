package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
}
