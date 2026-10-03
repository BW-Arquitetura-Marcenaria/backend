package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.StatusProjeto;

import java.math.BigDecimal;

public record ProjetoGetResponseDTO(
        Integer id,
        String tituloProjeto,
        BigDecimal valorTotal,
        StatusProjeto status
) {
}
