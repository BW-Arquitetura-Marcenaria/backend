package br.com.bw.backend.dto.response;

import java.math.BigDecimal;

public record CadastroValidarGetResponseDTO(
        String nome,
        String ambiente,
        String movel,
        String cor,
        String material,
        BigDecimal orcamento,
        String situacaoProjeto,
        Boolean precisaVisitaTecnica
) {}