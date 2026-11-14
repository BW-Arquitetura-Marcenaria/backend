package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.AssinaturaSignatarioPostRequestDTO;
import br.com.bw.backend.dto.response.AssinaturaContratoGetResponseDTO;
import br.com.bw.backend.mapper.AssinaturaContratoMapper;
import br.com.bw.backend.service.AssinaturaContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/projetos/{projetoId}/assinaturas")
@RequiredArgsConstructor
@Tag(name = "Assinaturas (Clicksign)", description = "Envio de contratos de um projeto para assinatura eletrônica via Clicksign")
@SecurityRequirement(name = "bearerAuth")
public class AssinaturaContratoController {

    private final AssinaturaContratoService assinaturaContratoService;
    private final AssinaturaContratoMapper mapper;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(
            summary = "Enviar contrato de um projeto para assinatura eletrônica",
            description = "Faz o upload do arquivo do contrato (PDF) e envia para os signatários informados "
                    + "assinarem eletronicamente via Clicksign. Apenas ADMIN pode enviar contratos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrato enviado para assinatura com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo ou signatários inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "502", description = "Falha ao se comunicar com a API da Clicksign")
    })
    public ResponseEntity<AssinaturaContratoGetResponseDTO> send(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer projetoId,
            @Parameter(description = "Arquivo do contrato (PDF)", required = true)
            @RequestPart("arquivo") MultipartFile arquivo,
            @Parameter(description = "Lista de signatários (nome, email, cpf e telefone)", required = true)
            @RequestPart("signatarios") @Valid List<AssinaturaSignatarioPostRequestDTO> signatarios) {

        var assinatura = assinaturaContratoService.sendForSignature(projetoId, arquivo, signatarios);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toAssinaturaContratoGetResponseDTO(assinatura));
    }

    @GetMapping
    @Operation(
            summary = "Listar assinaturas de contrato de um projeto",
            description = "Retorna o histórico de envios para assinatura eletrônica do projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assinaturas retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public List<AssinaturaContratoGetResponseDTO> list(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer projetoId) {

        return mapper.toAssinaturaContratoGetResponseDTO(assinaturaContratoService.listByProjectId(projetoId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar assinatura de contrato",
            description = "Retorna os detalhes e o status atual de uma assinatura de contrato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assinatura retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Projeto ou assinatura não encontrados")
    })
    public AssinaturaContratoGetResponseDTO get(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer projetoId,
            @Parameter(description = "Identificador da assinatura", required = true)
            @PathVariable Integer id) {

        return mapper.toAssinaturaContratoGetResponseDTO(
                assinaturaContratoService.findByProjectAndId(projetoId, id));
    }
}
