package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record LeadInstagramPostRequestDTO(
        @Size(max = 20) String origem,
        @Size(max = 40) String instagramId,
        Integer usuarioId,
        String crmLeadId,
        String nome,
        Boolean saoPaulo,
        String ambiente,
        String movel,
        String cor,
        String material,
        BigDecimal orcamento,
        String situacaoProjeto,
        Boolean precisaVisitaTecnica,
        String resumoConversa,
        @Min(0) @Max(100) Integer score,
        @Size(max = 20) String classificacao
) {}