package br.com.bw.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReferenciaGetResponseDTO(
        Integer id,
        String itemService,
        BigDecimal precoReferencia,
        String descricao,
        LocalDateTime dataAtualizacao
) {
}
