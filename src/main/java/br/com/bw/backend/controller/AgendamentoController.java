package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.DecisaoAgendamentoRequest;
import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
import br.com.bw.backend.dto.response.AgendamentoResponse;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AgendamentoMapper agendamentoMapper;

    @PostMapping
    public ResponseEntity<AgendamentoResponse> solicitar(
            Authentication authentication,
            @RequestBody @Valid SolicitacaoAgendamentoRequest request
    ) {
        Integer usuarioId = (Integer) authentication.getPrincipal();

        Agendamento agendamento = agendamentoService.solicitar(
                usuarioId,
                request.titulo(),
                request.descricao(),
                request.inicio(),
                request.fim()
        );

        AgendamentoResponse response = agendamentoMapper.toResponse(agendamento);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgendamentoResponse> criarComoAdmin(
            Authentication authentication,
            @RequestBody @Valid SolicitacaoAgendamentoRequest request
    ) {
        Integer adminId = (Integer) authentication.getPrincipal();

        Agendamento agendamento = agendamentoService.criarEAprovarComoAdmin(
                adminId,
                request.titulo(),
                request.descricao(),
                request.inicio(),
                request.fim(),
                "Criado diretamente pelo admin."
        );

        AgendamentoResponse response = agendamentoMapper.toResponse(agendamento);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AgendamentoResponse>> listarTodos() {
        List<AgendamentoResponse> respostas = agendamentoService
                .listarTodos()
                .stream()
                .map(agendamentoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(respostas);
    }

    @PatchMapping("/{id}/aprovado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgendamentoResponse> aprovar(
            @PathVariable Integer id,
            @Valid @RequestBody DecisaoAgendamentoRequest request
    ) {
        Agendamento agendamento = agendamentoService.aprovar(
                id,
                request.observacao()
        );

        return ResponseEntity.ok(agendamentoMapper.toResponse(agendamento));
    }

    @PatchMapping("/{id}/recusado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgendamentoResponse> recusar(
            @PathVariable Integer id,
            @Valid @RequestBody DecisaoAgendamentoRequest request
    ) {
        Agendamento agendamento = agendamentoService.recusar(
                id,
                request.observacao()
        );

        return ResponseEntity.ok(agendamentoMapper.toResponse(agendamento));
    }
}

