package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatLogPostRequestDTO(
        @NotBlank @Size(max = 40) String instagramId,
        @NotBlank @Size(max = 10000) String mensagemUsuario,
        @NotBlank @Size(max = 10000) String respostaIa
) {}
