package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.enums.CategoriaReferencia;
import br.com.bw.backend.entity.enums.UnidadeReferencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReferenciaPostRequestDTO(
        @NotNull
        CategoriaReferencia categoria,
        @NotNull
        @PositiveOrZero
        BigDecimal precoReferencia,
        @NotBlank
        String descricao,
        @NotBlank
        @Length(min = 0, max = 150)
        String materialBase,
        @NotNull
        UnidadeReferencia unidade,
        @PastOrPresent
        LocalDateTime dataAtualizacao
) {
}
