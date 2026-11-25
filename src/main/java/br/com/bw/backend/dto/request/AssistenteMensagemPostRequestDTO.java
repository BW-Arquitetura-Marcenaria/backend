package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistenteMensagemPostRequestDTO(
        @NotBlank @Size(max = 2000)
        String mensagem
) {
}