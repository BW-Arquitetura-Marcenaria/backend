package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.RemetenteChat;

import java.time.LocalDateTime;

public record MensagemChatGetResponseDTO(
        Integer id,
        RemetenteChat remetente,
        String conteudo,
        LocalDateTime criadoEm
) {
}
