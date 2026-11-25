package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.AssistenteMensagemPostRequestDTO;
import br.com.bw.backend.dto.response.AssistenteHistoricoGetResponseDTO;
import br.com.bw.backend.dto.response.AssistenteMensagemPostResponseDTO;
import br.com.bw.backend.service.AssistenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/chat/assistente")
@RequiredArgsConstructor
@Tag(name = "Assistente", description = "Conversa do cliente com o assistente de IA dentro da plataforma (apenas CLIENTE)")
@SecurityRequirement(name = "bearerAuth")
public class AssistenteController {

    private final AssistenteService service;

    @GetMapping
    @Operation(
            summary = "Histórico da conversa com o assistente",
            description = "Retorna as mensagens trocadas com o assistente e se o atendimento já foi concluído (resumo confirmado pelo cliente).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente")
    })
    public AssistenteHistoricoGetResponseDTO findHistory(@AuthenticationPrincipal Integer usuarioId) {
        return service.findHistory(usuarioId);
    }

    @PostMapping("/mensagens")
    @Operation(
            summary = "Enviar mensagem ao assistente",
            description = "Envia a mensagem do cliente ao assistente de IA e devolve a resposta. "
                    + "Na primeira mensagem o cliente passa de Novo Lead para Em Qualificação. "
                    + "O campo concluido indica que o cliente confirmou o resumo do projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta do assistente"),
            @ApiResponse(responseCode = "400", description = "Mensagem vazia ou longa demais"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é cliente"),
            @ApiResponse(responseCode = "502", description = "Assistente indisponível")
    })
    public AssistenteMensagemPostResponseDTO sendMessage(@AuthenticationPrincipal Integer usuarioId,
                                                         @RequestBody @Valid AssistenteMensagemPostRequestDTO dto) {
        return service.sendMessage(usuarioId, dto);
    }
}