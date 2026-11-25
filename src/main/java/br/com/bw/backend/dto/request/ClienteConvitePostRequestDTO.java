package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record ClienteConvitePostRequestDTO(
        @NotBlank
        @Length(max = 200)
        String nomeCompleto,
        @NotBlank
        @Email
        @Length(max = 150)
        String email,
        @Length(max = 255)
        String tipoProjeto,
        @PositiveOrZero
        BigDecimal valor
) {
}