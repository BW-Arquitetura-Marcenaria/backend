package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AssinaturaSignatarioPostRequestDTO(
        @NotBlank(message = "Nome do signatário é obrigatório")
        String nome,

        @NotBlank(message = "E-mail do signatário é obrigatório")
        @Email(message = "E-mail do signatário inválido")
        String email,

        String cpf,

        String telefone
) {
}
