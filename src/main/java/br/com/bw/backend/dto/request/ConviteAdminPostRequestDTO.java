package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ConviteAdminPostRequestDTO(
        @NotBlank
        @Length(min = 0, max = 200)
        String nomeCompleto,
        @NotBlank
        @Email
        @Length(min = 0, max = 150)
        String email,
        @NotBlank
        @Length(min = 0, max = 20)
        String cpf
) {
}
