package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.enums.StatusProjeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProjetoPutRequestDTO(
        @NotBlank
        Integer id,
        @NotBlank
        String tituloProjeto,
        @PositiveOrZero
        BigDecimal valorTotal,
        StatusProjeto status
) {
}
