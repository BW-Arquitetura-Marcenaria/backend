package br.com.bw.backend.dto.response;

import java.math.BigDecimal;

public record FinanceiroKpiGetResponseDTO(
        BigDecimal receitasMes,
        BigDecimal variacaoReceitasPercentual,
        BigDecimal despesasMes,
        BigDecimal variacaoDespesasPercentual,
        BigDecimal resultadoLiquido,
        BigDecimal margemPercentual
) {
}
