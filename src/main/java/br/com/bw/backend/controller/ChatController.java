package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MensagemChatPostRequestDTO;
import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.dto.response.NaoLidasGetResponseDTO;
import br.com.bw.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat (Cliente)", description = "Conversa de chat do lado do cliente")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService service;

    @GetMapping("/mensagens")
    @Operation(
            summary = "Histórico de mensagens do cliente logado",
            description = "Retorna as mensagens da conversa do cliente logado, opcionalmente a partir de um id de corte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente")
    })
    public List<MensagemChatGetResponseDTO> getHistory(@AuthenticationPrincipal Integer usuarioId,
                                                      @Parameter(description = "Id da última mensagem já carregada (paginação incremental)")
                                                      @RequestParam(defaultValue = "0") Integer depois) {
        return service.getClientHistory(usuarioId, depois);
    }

    @PostMapping("/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Enviar mensagem",
            description = "Envia uma mensagem do cliente logado para o atendimento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Mensagem inválida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente")
    })
    public MensagemChatGetResponseDTO send(@AuthenticationPrincipal Integer usuarioId,
                                             @RequestBody @Valid MensagemChatPostRequestDTO dto) {
        return service.sendAsClient(usuarioId, dto);
    }

    @GetMapping("/nao-lidas")
    @Operation(
            summary = "Contagem de mensagens não lidas",
            description = "Retorna a quantidade de mensagens não lidas pelo cliente logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente")
    })
    public NaoLidasGetResponseDTO getUnread(@AuthenticationPrincipal Integer usuarioId) {
        return service.getClientUnread(usuarioId);
    }

    @PutMapping("/mensagens/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Marcar mensagens como lidas",
            description = "Marca todas as mensagens recebidas pelo cliente logado como lidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mensagens marcadas como lidas"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente")
    })
    public void markAsRead(@AuthenticationPrincipal Integer usuarioId) {
        service.markAsReadByClient(usuarioId);
    }
}