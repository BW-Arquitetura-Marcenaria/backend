package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.LeadInstagramPostRequestDTO;
import br.com.bw.backend.dto.response.LeadInstagramPostResponseDTO;
import br.com.bw.backend.service.LeadInstagramService;
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
@RequestMapping("v1/api/integracoes/leads")
@RequiredArgsConstructor
@Tag(name = "Integração - Leads", description = "Captura de leads do Instagram pelo agente de IA (automação externa)")
@SecurityRequirement(name = "apiKeyAuth")
public class LeadIntegracaoController {

    private final LeadInstagramService service;

    @PostMapping
    @Operation(
            summary = "Registrar lead capturado no Instagram",
            description = "Cria ou atualiza um lead a partir dos dados coletados pelo agente de IA durante a conversa no Instagram.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead registrado/atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Chave de API ausente ou inválida")
    })
    public ResponseEntity<LeadInstagramPostResponseDTO> register(
            @Valid @RequestBody LeadInstagramPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.register(dto));
    }
}