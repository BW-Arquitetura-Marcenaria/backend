package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ClienteConvitePostRequestDTO;
import br.com.bw.backend.service.ClienteConviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/funil/convites")
@RequiredArgsConstructor
@Tag(name = "Convite de Cliente", description = "Convite de novos clientes para a plataforma, feito pelo administrador a partir do funil (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class ClienteConviteController {

    private final ClienteConviteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Convidar cliente para a plataforma",
            description = "Registra o lead com os dados informados pelo administrador e envia ao cliente um e-mail com o link para completar o cadastro. "
                    + "Se o e-mail já foi convidado e ainda não se cadastrou, o convite é reenviado com um novo link.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Convite enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado"),
            @ApiResponse(responseCode = "502", description = "Falha ao enviar o e-mail")
    })
    public void invite(@AuthenticationPrincipal Integer adminId,
                       @RequestBody @Valid ClienteConvitePostRequestDTO dto) {
        service.invite(adminId, dto);
    }
}