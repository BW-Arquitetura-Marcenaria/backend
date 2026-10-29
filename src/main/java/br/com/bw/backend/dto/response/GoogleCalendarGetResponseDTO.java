package br.com.bw.backend.dto.response;

public record GoogleCalendarGetResponseDTO(
        String id,
        String nome,
        String fusoHorario,
        boolean principal
) {
}
