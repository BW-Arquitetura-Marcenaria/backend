package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReferenciaPostRequestDTO(
        @NotBlank
        @Length(min = 0, max = 150)
        String itemService,
        @NotNull
        @PositiveOrZero
        BigDecimal precoReferencia,
        @NotBlank
        String descricao,
        @NotNull
        @PastOrPresent
        LocalDateTime dataAtualizacao
) {
}
