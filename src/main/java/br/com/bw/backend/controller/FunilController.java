package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.FunilEtapaPutRequestDTO;
import br.com.bw.backend.dto.response.FunilDetalheGetResponseDTO;
import br.com.bw.backend.dto.response.FunilGetResponseDTO;
import br.com.bw.backend.service.FunilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/funil")
@RequiredArgsConstructor
@Tag(name = "Funil de Vendas", description = "Acompanhamento e movimentação de clientes/leads pelo funil de vendas (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class FunilController {

    private final FunilService service;

    @GetMapping
    @Operation(
            summary = "Listar funil de vendas",
            description = "Retorna a visão geral do funil, com a quantidade de clientes/leads em cada etapa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funil retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public FunilGetResponseDTO findAll() {
        return service.findAll();
    }

    @GetMapping("/{clienteId}")
    @Operation(
            summary = "Detalhar cliente no funil",
            description = "Retorna os detalhes do cliente/lead informado dentro do funil de vendas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public FunilDetalheGetResponseDTO findDetailsById(
            @Parameter(description = "Identificador do cliente", required = true)
            @PathVariable Integer clienteId) {
        return service.findDetailsById(clienteId);
    }

    @PutMapping("/{clienteId}/etapa")
    @Operation(
            summary = "Atualizar etapa do cliente no funil",
            description = "Move o cliente/lead informado para outra etapa do funil de vendas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etapa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Etapa inválida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public FunilDetalheGetResponseDTO updateStage(@AuthenticationPrincipal Integer adminId,
                                                   @Parameter(description = "Identificador do cliente", required = true)
                                                   @PathVariable Integer clienteId,
                                                   @RequestBody @Valid FunilEtapaPutRequestDTO dto) {
        return service.updateStage(adminId, clienteId, dto);
    }
}