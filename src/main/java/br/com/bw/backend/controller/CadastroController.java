package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.CadastroPostRequestDTO;
import br.com.bw.backend.dto.response.CadastroGetResponseDTO;
import br.com.bw.backend.dto.response.CadastroValidarGetResponseDTO;
import br.com.bw.backend.service.CadastroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/cadastro")
@RequiredArgsConstructor
public class CadastroController {

    private final CadastroService service;

    @GetMapping("/validar")
    public ResponseEntity<CadastroValidarGetResponseDTO> validate(@RequestParam String token) {
        return ResponseEntity.status(HttpStatus.OK).body(service.validate(token));
    }

    @PostMapping
    public ResponseEntity<CadastroGetResponseDTO> register(
            @Valid @RequestBody CadastroPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(dto));
    }
}