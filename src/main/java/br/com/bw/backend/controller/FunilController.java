package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.FunilEtapaPutRequestDTO;
import br.com.bw.backend.dto.response.FunilDetalheGetResponseDTO;
import br.com.bw.backend.dto.response.FunilGetResponseDTO;
import br.com.bw.backend.service.FunilService;
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
public class FunilController {

    private final FunilService service;

    @GetMapping
    public FunilGetResponseDTO listar() {
        return service.listar();
    }

    @GetMapping("/{clienteId}")
    public FunilDetalheGetResponseDTO detalhar(@PathVariable Integer clienteId) {
        return service.detalhar(clienteId);
    }

    @PutMapping("/{clienteId}/etapa")
    public FunilDetalheGetResponseDTO alterarEtapa(@AuthenticationPrincipal Integer adminId,
                                                   @PathVariable Integer clienteId,
                                                   @RequestBody @Valid FunilEtapaPutRequestDTO dto) {
        return service.alterarEtapa(adminId, clienteId, dto);
    }
}