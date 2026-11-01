package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.AgendamentoDecisaoPutRequestDTO;
import br.com.bw.backend.dto.request.AgendamentoPostRequestDTO;
import br.com.bw.backend.dto.response.AgendamentoGetResponseDTO;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.service.AgendamentoService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Solicitação e aprovação de agendamentos (visitas, entregas e montagens)")
@SecurityRequirement(name = "bearerAuth")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AgendamentoMapper agendamentoMapper;

    @PostMapping
    @Operation(
            summary = "Solicitar agendamento",
            description = "Permite que o usuário autenticado (cliente ou admin) solicite um novo agendamento, "
                    + "que ficará pendente de aprovação por um administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação de agendamento criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (datas, título, etc.)"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<AgendamentoGetResponseDTO> request(
            Authentication authentication,
            @RequestBody @Valid AgendamentoPostRequestDTO request
    ) {
        Integer usuarioId = (Integer) authentication.getPrincipal();

        Agendamento agendamento = agendamentoService.request(
                usuarioId,
                request.titulo(),
                request.descricao(),
                request.inicio(),
                request.fim()
        );

        AgendamentoGetResponseDTO response = agendamentoMapper.toResponse(agendamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Criar agendamento já aprovado (admin)",
            description = "Permite que um administrador crie um agendamento diretamente já aprovado, sem passar pela etapa de solicitação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento criado e aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (datas, título, etc.)"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<AgendamentoGetResponseDTO> createAsAdmin(
            Authentication authentication,
            @RequestBody @Valid AgendamentoPostRequestDTO request
    ) {
        Integer adminId = (Integer) authentication.getPrincipal();

        Agendamento agendamento = agendamentoService.createAndApproveAsAdmin(
                adminId,
                request.titulo(),
                request.descricao(),
                request.inicio(),
                request.fim(),
                "Criado diretamente pelo admin."
        );

        AgendamentoGetResponseDTO response = agendamentoMapper.toResponse(agendamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Listar todos os agendamentos (admin)",
            description = "Lista todos os agendamentos de todos os clientes, independentemente do status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<List<AgendamentoGetResponseDTO>> findAll() {
        List<AgendamentoGetResponseDTO> respostas = agendamentoService
                .findAll()
                .stream()
                .map(agendamentoMapper::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(respostas);
    }

    @PatchMapping("/{id}/aprovado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Aprovar agendamento",
            description = "Aprova uma solicitação de agendamento pendente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Agendamento não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AgendamentoGetResponseDTO> approve(
            @Parameter(description = "Identificador do agendamento", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody AgendamentoDecisaoPutRequestDTO request
    ) {
        Agendamento agendamento = agendamentoService.approve(
                id,
                request.observacao()
        );

        return ResponseEntity.status(HttpStatus.OK).body(agendamentoMapper.toResponse(agendamento));
    }

    @PatchMapping("/{id}/recusado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Recusar agendamento",
            description = "Recusa uma solicitação de agendamento pendente, registrando uma observação opcional.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento recusado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Agendamento não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AgendamentoGetResponseDTO> reject(
            @Parameter(description = "Identificador do agendamento", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody AgendamentoDecisaoPutRequestDTO request
    ) {
        Agendamento agendamento = agendamentoService.reject(
                id,
                request.observacao()
        );

        return ResponseEntity.status(HttpStatus.OK).body(agendamentoMapper.toResponse(agendamento));
    }
}
