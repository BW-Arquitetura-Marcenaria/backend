package br.com.bw.backend.repository;

import br.com.bw.backend.entity.CredenciaisGoogleCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredenciaisGoogleCalendarRepository extends JpaRepository<CredenciaisGoogleCalendar, Integer> {
    Optional<CredenciaisGoogleCalendar> findFirstByOrderByIdAsc();
}
