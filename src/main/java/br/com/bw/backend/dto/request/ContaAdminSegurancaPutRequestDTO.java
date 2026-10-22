package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaAdminSegurancaPutRequestDTO(
        @NotBlank @Size(max = 20)
        String cnpj,
        @NotBlank @Size(max = 72)
        String senhaAtual,
        @NotBlank @Size(max = 72)
        String novaSenha,
        @NotBlank @Size(max = 72)
        String confirmarNovaSenha
) {}