package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MensagemChatPostRequestDTO;
import br.com.bw.backend.dto.response.ConversaChatGetResponseDTO;
import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.dto.response.NaoLidasGetResponseDTO;
import br.com.bw.backend.dto.response.ResumoAtendimentoGetResponseDTO;
import br.com.bw.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/chat")
@RequiredArgsConstructor
public class AdminChatController {

    private final ChatService service;

    @GetMapping("/conversas")
    public List<ConversaChatGetResponseDTO> listConversations(@AuthenticationPrincipal Integer adminId) {
        return service.listConversations(adminId);
    }

    @GetMapping("/conversas/{clienteId}/mensagens")
    public List<MensagemChatGetResponseDTO> getHistory(@AuthenticationPrincipal Integer adminId,
                                                      @PathVariable Integer clienteId,
                                                      @RequestParam(defaultValue = "0") Integer depois) {
        return service.getAdminHistory(adminId, clienteId, depois);
    }

    @PostMapping("/conversas/{clienteId}/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    public MensagemChatGetResponseDTO reply(@AuthenticationPrincipal Integer adminId,
                                                @PathVariable Integer clienteId,
                                                @RequestBody @Valid MensagemChatPostRequestDTO dto) {
        return service.reply(adminId, clienteId, dto);
    }

    @GetMapping("/conversas/{clienteId}/resumo")
    public ResumoAtendimentoGetResponseDTO getSummary(@AuthenticationPrincipal Integer adminId,
                                                  @PathVariable Integer clienteId) {
        return service.getAttendanceSummary(adminId, clienteId);
    }

    @GetMapping("/nao-lidas")
    public NaoLidasGetResponseDTO getUnread(@AuthenticationPrincipal Integer adminId) {
        return service.getAdminUnread(adminId);
    }

    @PutMapping("/conversas/{clienteId}/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@AuthenticationPrincipal Integer adminId,
                                @PathVariable Integer clienteId) {
        service.markAsReadByAdmin(adminId, clienteId);
    }
}