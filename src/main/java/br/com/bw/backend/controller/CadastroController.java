package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.CadastroPostRequestDTO;
import br.com.bw.backend.dto.response.CadastroGetResponseDTO;
import br.com.bw.backend.dto.response.CadastroValidarGetResponseDTO;
import br.com.bw.backend.service.CadastroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cadastro", description = "Cadastro público de novos clientes a partir de um link de convite")
public class CadastroController {

    private final CadastroService service;

    @GetMapping("/validar")
    @Operation(
            summary = "Validar token de cadastro",
            description = "Verifica se o token de cadastro enviado por e-mail/instagram ainda é válido e retorna os dados pré-preenchidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    public ResponseEntity<CadastroValidarGetResponseDTO> validate(
            @Parameter(description = "Token de cadastro recebido no link", required = true)
            @RequestParam String token) {
        return ResponseEntity.status(HttpStatus.OK).body(service.validate(token));
    }

    @PostMapping
    @Operation(
            summary = "Concluir cadastro",
            description = "Conclui o cadastro do cliente a partir de um token válido, definindo senha e dados pessoais.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastro concluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou token expirado"),
            @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrados")
    })
    public ResponseEntity<CadastroGetResponseDTO> register(
            @Valid @RequestBody CadastroPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(dto));
    }
}