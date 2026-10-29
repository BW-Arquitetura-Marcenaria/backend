package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaAdminEmpresaPutRequestDTO(
        @NotBlank @Size(max = 150)
        String nome,
        @Size(max = 150)
        String nomeFantasia,
        @NotBlank @Size(max = 20)
        String telefone
) {}
