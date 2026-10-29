package br.com.bw.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record FunilGetResponseDTO(
        long oportunidadesAtivas,
        BigDecimal pipeline,
        int scoreMedio,
        List<FunilColunaGetResponseDTO> colunas
) {
}
