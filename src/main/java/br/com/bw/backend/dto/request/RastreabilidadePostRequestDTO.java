package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.Projeto;

import java.time.LocalDateTime;

public record RastreabilidadePostRequestDTO(
        String lote,
        String tipoMadeira,
        String fornecedor,
        String origem,
        String certificacoes,
        LocalDateTime dataRegistro,
        Projeto projeto
) {
}
