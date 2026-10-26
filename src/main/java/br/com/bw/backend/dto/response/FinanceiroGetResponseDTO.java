package br.com.bw.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroGetResponseDTO(
        Integer id,
        String descricao,
        Integer quantidade,
        BigDecimal valor,
        BigDecimal frete,
        BigDecimal valorTotal,
        BigDecimal valorFinalDesconto,
        BigDecimal valorEntrada,
        BigDecimal receberEntrega,
        BigDecimal valorRecebidoFinal,
        BigDecimal valorTotalLiquido,
        LocalDate dataPedido,
        LocalDate dataEntrega,
        String contatoCliente,
        String regiaoVenda
) {
}
