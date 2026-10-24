package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SolicitacaoAgendamentoRequest(
        @NotBlank
        @Size(max = 150)
        String titulo,
        @Size(max = 5000)
        String descricao,
        @NotNull
        @Future
        LocalDateTime inicio,
        @NotNull
        @Future
        LocalDateTime fim
) {
}
