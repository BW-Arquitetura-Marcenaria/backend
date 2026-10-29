package br.com.bw.backend.dto.response;

import java.math.BigDecimal;

public record FinanceiroReceitaDespesaMensalGetResponseDTO(
        String mes,
        BigDecimal receitas,
        BigDecimal despesas
) {
}
