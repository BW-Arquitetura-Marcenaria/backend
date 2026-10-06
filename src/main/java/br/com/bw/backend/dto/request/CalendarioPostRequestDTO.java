package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.enums.TipoEvento;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record CalendarioPostRequestDTO(
        @NotBlank
        TipoEvento tipoEvento,
        @NotBlank
        @PastOrPresent
        LocalDateTime dataInicio,
        @NotBlank
        @FutureOrPresent
        LocalDateTime dataFim,
        String observacoes
) {
}
