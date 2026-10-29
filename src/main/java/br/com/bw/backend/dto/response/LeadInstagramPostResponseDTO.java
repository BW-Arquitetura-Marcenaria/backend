package br.com.bw.backend.dto.response;

public record LeadInstagramPostResponseDTO(
        boolean jaCadastrado,
        boolean linkJaEnviado,
        String link
) {}
