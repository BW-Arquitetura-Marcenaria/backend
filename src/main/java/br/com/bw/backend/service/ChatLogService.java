package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.ChatLogPostRequestDTO;
import br.com.bw.backend.entity.ChatbotLog;
import br.com.bw.backend.repository.ChatbotLogRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatLogService {

    private final ChatbotLogRepository chatbotLogRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void register(ChatLogPostRequestDTO dto) {
        String instagramId = dto.instagramId().trim();

        ChatbotLog chatbotLog = new ChatbotLog();
        chatbotLog.setInstagramId(instagramId);
        chatbotLog.setMensagemUsuario(dto.mensagemUsuario());
        chatbotLog.setRespostaIa(dto.respostaIa());

        usuarioRepository.findByInstagramId(instagramId).ifPresent(chatbotLog::setCliente);

        chatbotLogRepository.save(chatbotLog);
        log.info("Log de chat registrado com sucesso - instagramId: {}", instagramId);
    }
}