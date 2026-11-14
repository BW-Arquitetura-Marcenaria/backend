package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.StatusSignatario;

public record AssinaturaSignatarioGetResponseDTO(
        Integer id,
        String nome,
        String email,
        StatusSignatario status
) {
}
