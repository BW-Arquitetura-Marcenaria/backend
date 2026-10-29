package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.LeadInstagramPostRequestDTO;
import br.com.bw.backend.dto.response.LeadInstagramPostResponseDTO;
import br.com.bw.backend.service.LeadInstagramService;
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
public class LeadIntegracaoController {

    private final LeadInstagramService service;

    @PostMapping
    public ResponseEntity<LeadInstagramPostResponseDTO> register(
            @Valid @RequestBody LeadInstagramPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.register(dto));
    }
}