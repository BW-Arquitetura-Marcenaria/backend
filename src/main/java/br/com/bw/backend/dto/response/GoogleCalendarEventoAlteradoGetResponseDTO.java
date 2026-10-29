package br.com.bw.backend.dto.response;

import java.time.LocalDateTime;

public record GoogleCalendarEventoAlteradoGetResponseDTO(
        String id,
        String status,
        LocalDateTime inicio,
        LocalDateTime fim
) {
}
