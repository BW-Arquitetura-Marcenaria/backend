package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.ChatLogPostRequestDTO;
import br.com.bw.backend.entity.ChatbotLog;
import br.com.bw.backend.repository.ChatbotLogRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final ChatbotLogRepository chatbotLogRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void registrarInstagram(ChatLogPostRequestDTO dto) {
        String instagramId = dto.instagramId().trim();

        ChatbotLog log = new ChatbotLog();
        log.setInstagramId(instagramId);
        log.setMensagemUsuario(dto.mensagemUsuario());
        log.setRespostaIa(dto.respostaIa());

        usuarioRepository.findByInstagramId(instagramId).ifPresent(log::setCliente);

        chatbotLogRepository.save(log);
    }
}