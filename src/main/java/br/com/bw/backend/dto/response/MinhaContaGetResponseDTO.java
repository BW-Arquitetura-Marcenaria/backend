package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.TipoCliente;

import java.time.LocalDate;

public record MinhaContaGetResponseDTO(
        TipoCliente tipoCliente,
        String nome,
        String sobrenome,
        String cpf,
        LocalDate dataNascimento,
        String email,
        String telefone,
        String origemContato,
        String instagramUsername,
        EmpresaResumo empresa
) {
    public record EmpresaResumo(
            String cnpj,
            String razaoSocial,
            String nomeFantasia,
            String telefone
    ) {}
}
