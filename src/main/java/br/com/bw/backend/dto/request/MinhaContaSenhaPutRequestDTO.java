package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MinhaContaSenhaPutRequestDTO(
        @NotBlank
        String senhaAtual,
        @NotBlank @Size(min = 8, max = 72)
        String novaSenha,
        @NotBlank
        String confirmarNovaSenha
) {}