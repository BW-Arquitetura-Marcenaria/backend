package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ChatLogPostRequestDTO;
import br.com.bw.backend.service.ChatLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/integracoes/chat-logs")
@RequiredArgsConstructor
public class ChatLogIntegracaoController {

    private final ChatLogService chatLogService;

    @PostMapping
    public ResponseEntity<Void> registrar(@Valid @RequestBody ChatLogPostRequestDTO dto) {
        chatLogService.registrarInstagram(dto);
        return ResponseEntity.status(201).build();
    }
}