package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusTransacao;
import br.com.bw.backend.entity.enums.TipoTransacao;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroPutRequestDTO(
        @NotBlank
        Integer id,
        @NotBlank
        @Length(min = 0, max = 10)
        TipoTransacao tipoTransacao,
        @NotNull
        @PositiveOrZero
        BigDecimal valor,
        @NotBlank
        @FutureOrPresent
        LocalDateTime dataTransacao,
        @NotBlank
        @Length(min = 0, max = 100)
        String categoria,
        @NotBlank
        String descricao,
        @NotBlank
        @Length(min = 0, max = 20)
        StatusTransacao status,
        @NotBlank
        Projeto projeto
) {
}
