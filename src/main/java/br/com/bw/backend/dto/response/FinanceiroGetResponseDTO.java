package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusTransacao;
import br.com.bw.backend.entity.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroGetResponseDTO(
        Integer id,
        TipoTransacao tipoTransacao,
        BigDecimal valor,
        LocalDateTime dataTransacao,
        String categoria,
        String descricao,
        StatusTransacao status,
        Projeto projeto
) {
}
