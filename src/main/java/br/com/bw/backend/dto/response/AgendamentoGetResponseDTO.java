package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.StatusAgendamento;

import java.time.LocalDateTime;

public record AgendamentoGetResponseDTO(
        Integer id,
        String titulo,
        String descricao,
        LocalDateTime inicio,
        LocalDateTime fim,
        StatusAgendamento status,
        String googleEventId
) {
}
