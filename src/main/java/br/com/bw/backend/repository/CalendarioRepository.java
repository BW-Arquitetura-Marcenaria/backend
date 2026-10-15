package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Calendario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarioRepository extends JpaRepository<Calendario, Integer> {
}
