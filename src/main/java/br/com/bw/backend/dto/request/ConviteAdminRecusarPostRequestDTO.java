package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConviteAdminRecusarPostRequestDTO(
        @NotBlank
        String token
) {
}