package br.com.bw.backend.event;

import br.com.bw.backend.dto.response.MensagemChatTempoRealResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatTempoRealListener {

    private static final String CANAL_CLIENTE = "/queue/chat";
    private static final String CANAL_EQUIPE = "/topic/admin/chat";

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MensagemChatCriadaEvent evento) {
        MensagemChatTempoRealResponseDTO payload = new MensagemChatTempoRealResponseDTO(evento.clienteId(), evento.mensagem());

        try {
            messagingTemplate.convertAndSendToUser(String.valueOf(evento.clienteId()), CANAL_CLIENTE, payload);
            messagingTemplate.convertAndSend(CANAL_EQUIPE, payload);
        } catch (Exception e) {
            log.warn("Mensagem {} não foi empurrada pelo WebSocket", evento.mensagem().id(), e);
        }
    }
}