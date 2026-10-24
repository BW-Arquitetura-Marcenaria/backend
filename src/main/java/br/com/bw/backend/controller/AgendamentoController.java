package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
import br.com.bw.backend.dto.response.AgendamentoResponse;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AgendamentoMapper agendamentoMapper;

    @PostMapping
    public ResponseEntity<AgendamentoResponse> solicitar(
            Authentication authentication,
            @RequestBody SolicitacaoAgendamentoRequest request
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
}

