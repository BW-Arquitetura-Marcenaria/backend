package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record MensagemChatPostRequestDTO(
        @NotBlank
        @Length(min = 0, max = 2000)
        String conteudo
) {
}