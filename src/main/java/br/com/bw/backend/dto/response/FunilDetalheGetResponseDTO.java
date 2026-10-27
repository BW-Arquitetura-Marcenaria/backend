package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.EtapaFunil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FunilDetalheGetResponseDTO(
        Integer clienteId,
        String nome,
        String iniciais,
        String tipoProjeto,
        BigDecimal orcamento,
        String origem,
        LocalDateTime recebidoEm,
        Integer score,
        String classificacao,
        EtapaFunil etapa
) {
}