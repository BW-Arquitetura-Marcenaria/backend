package br.com.bw.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FunilCardGetResponseDTO(
        Integer clienteId,
        String nome,
        String iniciais,
        String origem,
        LocalDateTime recebidoEm,
        String tipoProjeto,
        BigDecimal orcamento,
        Integer score,
        String classificacao
) {
}