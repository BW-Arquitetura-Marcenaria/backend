package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Size;

public record DecisaoAgendamentoRequest(
        @Size(max = 5000, message = "A observação deve ter no máximo 5000 caracteres.")
        String observacao
) {
}
