package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroPostRequestDTO(
        String descricao,
        @NotNull
        @Positive
        Integer quantidade,
        @NotNull
        @PositiveOrZero
        BigDecimal valor,
        @NotNull
        @PositiveOrZero
        BigDecimal frete,
        @NotNull
        @PositiveOrZero
        BigDecimal valorTotal,
        @PositiveOrZero
        BigDecimal valorFinalDesconto,
        @PositiveOrZero
        BigDecimal valorEntrada,
        @PositiveOrZero
        BigDecimal receberEntrega,
        @PositiveOrZero
        BigDecimal valorRecebidoFinal,
        @PositiveOrZero
        BigDecimal valorTotalLiquido,
        LocalDate dataPedido,
        LocalDate dataEntrega,
        @Length(max = 100)
        String contatoCliente,
        @Length(max = 100)
        String regiaoVenda
) {
}
