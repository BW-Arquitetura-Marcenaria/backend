package br.com.bw.backend.dto.response;

public record MensagemChatTempoRealResponseDTO(
        Integer clienteId,
        MensagemChatGetResponseDTO mensagem
) {
}