package br.com.bw.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AssistenteHistoricoGetResponseDTO(
        boolean concluido,
        List<Mensagem> mensagens
) {
    public record Mensagem(
            String autor,
            String texto,
            LocalDateTime enviadoEm
    ) {
    }
}