package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.Projeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RastreabilidadePostRequestDTO(
        @NotBlank
        String lote,
        @NotBlank
        String tipoMadeira,
        @NotBlank
        String fornecedor,
        String origem,
        String certificacoes,
        LocalDateTime dataRegistro,
        @NotNull
        Projeto projeto
) {
}
