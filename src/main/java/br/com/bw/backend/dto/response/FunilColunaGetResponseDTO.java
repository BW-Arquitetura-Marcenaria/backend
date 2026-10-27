package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.EtapaFunil;

import java.util.List;

public record FunilColunaGetResponseDTO(
        EtapaFunil etapa,
        String titulo,
        long total,
        List<FunilCardGetResponseDTO> cards
) {
}