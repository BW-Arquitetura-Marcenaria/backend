package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ConviteAdminAceitarPostRequestDTO;
import br.com.bw.backend.dto.request.ConviteAdminRecusarPostRequestDTO;
import br.com.bw.backend.dto.response.ConviteAdminValidacaoGetResponseDTO;
import br.com.bw.backend.service.ConviteAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/convite-admin")
@RequiredArgsConstructor
@Tag(name = "Convite de Administrador", description = "Fluxo público usado pelo convidado para validar, aceitar ou recusar um convite de administrador")
public class ConviteAdminController {

    private final ConviteAdminService service;

    @GetMapping("/validar")
    @Operation(
            summary = "Validar convite de administrador",
            description = "Verifica se o token do convite ainda é válido e retorna os dados do convidado para exibição na tela de aceite.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convite válido"),
            @ApiResponse(responseCode = "400", description = "Convite inválido, expirado ou já respondido")
    })
    public ConviteAdminValidacaoGetResponseDTO validate(
            @Parameter(description = "Token do convite recebido por e-mail", required = true)
            @RequestParam String token) {
        return service.validate(token);
    }

    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Aceitar convite de administrador",
            description = "Define a senha de acesso e cria o novo usuário administrador a partir de um convite válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Convite aceito e administrador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senhas não conferem, convite inválido ou expirado"),
            @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrados")
    })
    public void accept(@RequestBody @Valid ConviteAdminAceitarPostRequestDTO dto) {
        service.accept(dto);
    }

    @PostMapping("/recusar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Recusar convite de administrador",
            description = "Marca o convite como recusado, impedindo que o token seja usado novamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Convite recusado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Convite inválido, expirado ou já respondido")
    })
    public void reject(@RequestBody @Valid ConviteAdminRecusarPostRequestDTO dto) {
        service.reject(dto);
    }
}