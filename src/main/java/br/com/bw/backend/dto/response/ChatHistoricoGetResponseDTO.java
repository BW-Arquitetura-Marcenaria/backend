package br.com.bw.backend.dto.response;

import java.time.LocalDateTime;

public record ChatHistoricoGetResponseDTO(
        Integer id,
        String mensagemUsuario,
        String respostaIa,
        LocalDateTime dataInteracao
) {
}
