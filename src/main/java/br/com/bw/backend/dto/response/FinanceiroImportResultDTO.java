package br.com.bw.backend.dto.response;

import java.util.List;

public record FinanceiroImportResultDTO(
        int totalLinhasProcessadas,
        int totalImportado,
        List<String> erros
) {
}
