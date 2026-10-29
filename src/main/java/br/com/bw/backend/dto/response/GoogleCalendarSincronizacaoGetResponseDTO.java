package br.com.bw.backend.dto.response;

import java.util.List;

public record GoogleCalendarSincronizacaoGetResponseDTO(
        List<GoogleCalendarEventoAlteradoGetResponseDTO> eventos,
        String proximoSyncToken
) {
}
