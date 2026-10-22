package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaAdminEmpresaPutRequestDTO(
        @NotBlank @Size(max = 150)
        String nome,
        @NotBlank @Email @Size(max = 150)
        String email,
        @NotBlank @Size(max = 20)
        String telefone
) {}