package br.com.bw.backend.dto.response;

public record GoogleCalendarResponse(
        String id,
        String nome,
        String fusoHorario,
        boolean principal
) {
}
