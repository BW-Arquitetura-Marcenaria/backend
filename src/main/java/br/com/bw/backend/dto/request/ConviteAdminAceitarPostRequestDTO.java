package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ConviteAdminAceitarPostRequestDTO(
        @NotBlank
        String token,
        @NotBlank
        @Length(min = 8, max = 72)
        String senha,
        @NotBlank
        @Length(min = 8, max = 72)
        String confirmarSenha
) {
}
