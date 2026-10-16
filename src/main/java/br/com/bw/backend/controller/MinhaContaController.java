package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MinhaContaPutRequestDTO;
import br.com.bw.backend.dto.response.MinhaContaGetResponseDTO;
import br.com.bw.backend.service.MinhaContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/minha-conta")
@RequiredArgsConstructor
public class MinhaContaController {

    private final MinhaContaService minhaContaService;

    @GetMapping
    public ResponseEntity<MinhaContaGetResponseDTO> buscar() {
        return ResponseEntity.ok(minhaContaService.buscar());
    }

    @PutMapping
    public ResponseEntity<MinhaContaGetResponseDTO> atualizar(@Valid @RequestBody MinhaContaPutRequestDTO dto) {
        return ResponseEntity.ok(minhaContaService.atualizar(dto));
    }
}
