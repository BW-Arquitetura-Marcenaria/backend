package br.com.bw.backend.dto.response;

import java.util.List;

public record AdministradoresGetResponseDTO(
        long acessosAtivos,
        List<AdministradorItem> administradores
) {

    public record AdministradorItem(
            Integer id,
            String tipo,
            String nomeCompleto,
            String email,
            String cpf,
            String status
    ) {
    }
}
