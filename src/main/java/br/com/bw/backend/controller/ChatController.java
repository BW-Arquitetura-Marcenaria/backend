package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MensagemChatPostRequestDTO;
import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.dto.response.NaoLidasGetResponseDTO;
import br.com.bw.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;

    @GetMapping("/mensagens")
    public List<MensagemChatGetResponseDTO> getHistory(@AuthenticationPrincipal Integer usuarioId,
                                                      @RequestParam(defaultValue = "0") Integer depois) {
        return service.getClientHistory(usuarioId, depois);
    }

    @PostMapping("/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    public MensagemChatGetResponseDTO send(@AuthenticationPrincipal Integer usuarioId,
                                             @RequestBody @Valid MensagemChatPostRequestDTO dto) {
        return service.sendAsClient(usuarioId, dto);
    }

    @GetMapping("/nao-lidas")
    public NaoLidasGetResponseDTO getUnread(@AuthenticationPrincipal Integer usuarioId) {
        return service.getClientUnread(usuarioId);
    }

    @PutMapping("/mensagens/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@AuthenticationPrincipal Integer usuarioId) {
        service.markAsReadByClient(usuarioId);
    }
}