package br.com.bw.backend.dto.response;

import java.time.LocalDateTime;

public record ConviteAdminValidacaoGetResponseDTO(
        String nomeCompleto,
        String email,
        String nomeEmpresa,
        LocalDateTime expiraEm
) {
}