package br.com.bw.backend.dto.response;

import java.util.List;

public record FinanceiroImportPostResponseDTO(
        int totalLinhasProcessadas,
        int totalImportado,
        List<String> erros
) {
}
