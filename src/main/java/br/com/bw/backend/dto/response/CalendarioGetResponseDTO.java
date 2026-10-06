package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

public record CalendarioGetResponseDTO(
        Integer id,
        TipoEvento tipoEvento,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String observacoes
) {
}
