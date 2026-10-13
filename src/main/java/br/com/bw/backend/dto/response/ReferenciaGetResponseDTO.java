package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.CategoriaReferencia;
import br.com.bw.backend.entity.enums.UnidadeReferencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReferenciaGetResponseDTO(
        Integer id,
        CategoriaReferencia categoria,
        BigDecimal precoReferencia,
        String descricao,
        String materialBase,
        UnidadeReferencia unidade,
        LocalDateTime dataAtualizacao
) {
}
