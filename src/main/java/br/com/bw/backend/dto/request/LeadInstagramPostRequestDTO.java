package br.com.bw.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record LeadInstagramPostRequestDTO(
        @NotBlank String instagramId,
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
        String resumoConversa
) {}