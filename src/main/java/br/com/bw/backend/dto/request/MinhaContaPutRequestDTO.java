package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MinhaContaPutRequestDTO(
        @NotBlank @Size(max = 100)
        String nome,
        @NotBlank @Size(max = 100)
        String sobrenome,
        LocalDate dataNascimento,
        @NotBlank @Email @Size(max = 150)
        String email,
        @NotBlank @Size(max = 20)
        String telefone,
        @Size(max = 30)
        String origemContato,
        @Size(max = 60)
        String instagramUsername,
        @Size(max = 150)
        String razaoSocial,
        @Size(max = 150)
        String nomeFantasia,
        @Size(max = 72)
        String senhaAtual,
        @Size(max = 72)
        String novaSenha,
        @Size(max = 72)
        String confirmarNovaSenha
) {}