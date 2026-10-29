package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.AgendamentoDecisaoPutRequestDTO;
import br.com.bw.backend.dto.request.AgendamentoPostRequestDTO;
import br.com.bw.backend.dto.response.AgendamentoGetResponseDTO;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.service.AgendamentoService;
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
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AgendamentoMapper agendamentoMapper;

    @PostMapping
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
    public ResponseEntity<AgendamentoGetResponseDTO> approve(
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
    public ResponseEntity<AgendamentoGetResponseDTO> reject(
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
