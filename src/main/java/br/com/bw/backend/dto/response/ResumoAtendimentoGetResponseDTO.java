package br.com.bw.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResumoAtendimentoGetResponseDTO(
        String resumo,
        String ambiente,
        String movel,
        String cor,
        String material,
        BigDecimal orcamento,
        String situacaoProjeto,
        Boolean precisaVisitaTecnica,
        Boolean saoPaulo,
        LocalDateTime atendidoEm
) {
}
