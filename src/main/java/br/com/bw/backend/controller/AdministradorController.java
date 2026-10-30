package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ConviteAdminPostRequestDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO.AdministradorItem;
import br.com.bw.backend.service.ConviteAdminService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/administradores")
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "Gestão de administradores da empresa (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdministradorController {

    private final ConviteAdminService service;

    @GetMapping
    @Operation(
            summary = "Listar administradores",
            description = "Lista os administradores ativos/desativados da empresa do usuário logado, incluindo convites pendentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public AdministradoresGetResponseDTO findAll(@AuthenticationPrincipal Integer usuarioId) {
        return service.findAll(usuarioId);
    }

    @PostMapping("/convites")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Convidar novo administrador",
            description = "Envia um convite por e-mail para que a pessoa informada se torne administrador da mesma empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Convite enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (nome, CPF, etc.)"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "409", description = "E-mail/CPF já cadastrados ou já existe convite pendente")
    })
    public AdministradorItem invite(@AuthenticationPrincipal Integer usuarioId,
                                      @RequestBody @Valid ConviteAdminPostRequestDTO dto) {
        return service.invite(usuarioId, dto);
    }

    @PutMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Desativar administrador",
            description = "Desativa o acesso de um administrador da mesma empresa, impedindo novos logins.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Administrador desativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tentativa de desativar o próprio acesso ou o último administrador ativo"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    public void deactivate(@AuthenticationPrincipal Integer usuarioId,
                          @Parameter(description = "Identificador do administrador a ser desativado", required = true)
                          @PathVariable Integer id) {
        service.deactivate(usuarioId, id);
    }
}