package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record RastreabilidadePutRequestDTO(
        @NotBlank
        @Length(min = 0, max = 100)
        String lote,
        @NotBlank
        @Length(min = 0, max = 100)
        String tipoMadeira,
        @NotBlank
        @Length(min = 0, max = 150)
        String fornecedor,
        @NotBlank
        @Length(min = 0, max = 150)
        String origem,
        @NotBlank
        @Length(min = 0, max = 255)
        String certificacoes,
        @NotBlank
        @Length(min = 0, max = 100)
        String volume,
        LocalDate dataRegistro,
        @NotNull
        Integer projetoId
) {
}
