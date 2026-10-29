package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaAdminEnderecoPutRequestDTO(
        @NotBlank @Size(max = 9)
        String cep,
        @NotBlank @Size(max = 150)
        String logradouro,
        @NotBlank @Size(max = 20)
        String numero,
        @Size(max = 100)
        String complemento,
        @NotBlank @Size(max = 100)
        String bairro,
        @NotBlank @Size(max = 100)
        String cidade,
        @NotBlank @Size(min = 2, max = 2)
        String uf
) {}
