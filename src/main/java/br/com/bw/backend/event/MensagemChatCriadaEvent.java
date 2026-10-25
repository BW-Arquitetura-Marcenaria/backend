package br.com.bw.backend.event;

import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.entity.enums.RemetenteChat;

public record MensagemChatCriadaEvent(
        Integer clienteId,
        RemetenteChat remetente,
        boolean primeiraNaoLida,
        MensagemChatGetResponseDTO mensagem
) {
}