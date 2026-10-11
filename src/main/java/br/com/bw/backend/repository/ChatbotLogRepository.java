package br.com.bw.backend.repository;

import br.com.bw.backend.entity.ChatbotLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Integer> {
    List<ChatbotLog> findByInstagramIdOrderByDataInteracaoAsc(String instagramId);
}
