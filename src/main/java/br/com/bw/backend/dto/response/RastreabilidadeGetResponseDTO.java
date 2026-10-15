package br.com.bw.backend.dto.response;

import java.time.LocalDateTime;

public record RastreabilidadeGetResponseDTO(
        Integer id,
        String lote,
        String tipoMadeira,
        String fornecedor,
        String origem,
        String certificacoes,
        LocalDateTime dataRegistro,
        ProjetoGetResponseDTO projeto
) {
}
