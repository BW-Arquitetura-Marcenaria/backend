package br.com.bw.backend.dto.response;

import java.util.List;

public record ResultadoSincronizacaoGoogle(
        List<EventoAlteradoGoogleResponse> eventos,
        String proximoSyncToken
) {
}
