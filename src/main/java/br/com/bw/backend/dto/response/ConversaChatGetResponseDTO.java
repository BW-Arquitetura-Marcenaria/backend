package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.RemetenteChat;

import java.time.LocalDateTime;

public record ConversaChatGetResponseDTO(
        Integer clienteId,
        String nomeCliente,
        String ultimaMensagem,
        RemetenteChat ultimoRemetente,
        LocalDateTime ultimaMensagemEm,
        long naoLidas
) {
}
