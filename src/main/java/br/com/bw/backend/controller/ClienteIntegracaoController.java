package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.ClienteInstagramGetResponseDTO;
import br.com.bw.backend.service.ClienteIntegracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/integracoes/clientes")
@RequiredArgsConstructor
@Tag(name = "Integração - Clientes", description = "Consulta de clientes a partir de dados do Instagram (automação externa)")
@SecurityRequirement(name = "apiKeyAuth")
public class ClienteIntegracaoController {

    private final ClienteIntegracaoService service;

    @GetMapping("/instagram/{instagramId}")
    @Operation(
            summary = "Buscar cliente pelo Instagram ID",
            description = "Retorna os dados do cliente/lead associado ao identificador do Instagram informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Chave de API ausente ou inválida"),
            @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado para o Instagram ID informado")
    })
    public ClienteInstagramGetResponseDTO findByInstagram(
            @Parameter(description = "Identificador do usuário no Instagram", required = true)
            @PathVariable String instagramId) {
        return service.findByInstagram(instagramId);
    }
}