package br.com.bw.backend.dto.request;

import java.time.LocalDateTime;

public record CriarEventoGoogleRequest(
        String titulo,
        String descricao,
        LocalDateTime inicio,
        LocalDateTime fim
) {
}
