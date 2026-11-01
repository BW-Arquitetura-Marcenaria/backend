package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MensagemChatPostRequestDTO;
import br.com.bw.backend.dto.response.ConversaChatGetResponseDTO;
import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.dto.response.NaoLidasGetResponseDTO;
import br.com.bw.backend.dto.response.ResumoAtendimentoGetResponseDTO;
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
@Tag(name = "Chat (Admin)", description = "Atendimento de conversas de chat pelo lado do administrador")
@SecurityRequirement(name = "bearerAuth")
public class AdminChatController {

    private final ChatService service;

    @GetMapping("/conversas")
    @Operation(
            summary = "Listar conversas",
            description = "Lista as conversas de chat de clientes atendidos pelo administrador logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public List<ConversaChatGetResponseDTO> listConversations(@AuthenticationPrincipal Integer adminId) {
        return service.listConversations(adminId);
    }

    @GetMapping("/conversas/{clienteId}/mensagens")
    @Operation(
            summary = "Histórico de mensagens com um cliente",
            description = "Retorna as mensagens trocadas com o cliente informado, opcionalmente a partir de um id de corte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public List<MensagemChatGetResponseDTO> getHistory(@AuthenticationPrincipal Integer adminId,
                                                      @Parameter(description = "Identificador do cliente", required = true)
                                                      @PathVariable Integer clienteId,
                                                      @Parameter(description = "Id da última mensagem já carregada (paginação incremental)")
                                                      @RequestParam(defaultValue = "0") Integer depois) {
        return service.getAdminHistory(adminId, clienteId, depois);
    }

    @PostMapping("/conversas/{clienteId}/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Responder cliente",
            description = "Envia uma mensagem do administrador logado para o cliente informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Mensagem inválida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public MensagemChatGetResponseDTO reply(@AuthenticationPrincipal Integer adminId,
                                                @Parameter(description = "Identificador do cliente", required = true)
                                                @PathVariable Integer clienteId,
                                                @RequestBody @Valid MensagemChatPostRequestDTO dto) {
        return service.reply(adminId, clienteId, dto);
    }

    @GetMapping("/conversas/{clienteId}/resumo")
    @Operation(
            summary = "Resumo do atendimento",
            description = "Retorna um resumo (gerado por IA ou regras) do atendimento prestado ao cliente informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResumoAtendimentoGetResponseDTO getSummary(@AuthenticationPrincipal Integer adminId,
                                                  @Parameter(description = "Identificador do cliente", required = true)
                                                  @PathVariable Integer clienteId) {
        return service.getAttendanceSummary(adminId, clienteId);
    }

    @GetMapping("/nao-lidas")
    @Operation(
            summary = "Contagem de mensagens não lidas",
            description = "Retorna a quantidade de mensagens não lidas pelo administrador logado, por conversa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public NaoLidasGetResponseDTO getUnread(@AuthenticationPrincipal Integer adminId) {
        return service.getAdminUnread(adminId);
    }

    @PutMapping("/conversas/{clienteId}/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Marcar conversa como lida",
            description = "Marca todas as mensagens do cliente informado como lidas pelo administrador logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conversa marcada como lida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public void markAsRead(@AuthenticationPrincipal Integer adminId,
                                @Parameter(description = "Identificador do cliente", required = true)
                                @PathVariable Integer clienteId) {
        service.markAsReadByAdmin(adminId, clienteId);
    }
}