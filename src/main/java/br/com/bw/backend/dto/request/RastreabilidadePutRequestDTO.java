package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RastreabilidadePutRequestDTO(
        @NotBlank
        Integer id,
        @NotBlank
        String lote,
        @NotBlank
        String tipoMadeira,
        @NotBlank
        String fornecedor,
        String origem,
        String certificacoes,
        LocalDate dataRegistro,
        @NotNull
        Integer projetoId
) {
}
