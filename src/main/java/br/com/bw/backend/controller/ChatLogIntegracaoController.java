package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ChatLogPostRequestDTO;
import br.com.bw.backend.service.ChatLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/integracoes/chat-logs")
@RequiredArgsConstructor
@Tag(name = "Integração - Logs de Chat", description = "Registro de interações do chatbot de IA (ex: n8n/Instagram)")
@SecurityRequirement(name = "apiKeyAuth")
public class ChatLogIntegracaoController {

    private final ChatLogService chatLogService;

    @PostMapping
    @Operation(
            summary = "Registrar log de interação do chatbot",
            description = "Grava a pergunta do usuário e a resposta gerada pela IA, usado pela automação externa de atendimento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Log registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Chave de API ausente ou inválida")
    })
    public ResponseEntity<Void> register(@Valid @RequestBody ChatLogPostRequestDTO dto) {
        chatLogService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}