package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.enums.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CadastroPostRequestDTO(
        @NotBlank
        String token,
        @NotNull
        TipoCliente tipoCliente,
        @NotBlank @Size(max = 100)
        String nome,
        @NotBlank @Size(max = 100)
        String sobrenome,
        @Size(max = 20)
        String cpf,
        @Size(max = 20)
        String cnpj,
        LocalDate dataNascimento,
        @NotBlank @Email @Size(max = 150)
        String email,
        @NotBlank @Size(max = 20)
        String telefone,
        @NotBlank @Size(min = 8, max = 72)
        String senha
) {}