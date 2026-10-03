package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.Projeto;

import java.time.LocalDateTime;

public record RastreabilidadeGetResponseDTO(
        Integer id,
        String lote,
        String tipoMadeira,
        String fornecedor,
        String origem,
        String certificacoes,
        LocalDateTime dataRegistro,
        Projeto projeto
) {
}
