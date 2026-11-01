package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.MinhaContaPutRequestDTO;
import br.com.bw.backend.dto.response.MinhaContaGetResponseDTO;
import br.com.bw.backend.service.MinhaContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/minha-conta")
@RequiredArgsConstructor
@Tag(name = "Minha Conta", description = "Dados da conta do cliente logado")
@SecurityRequirement(name = "bearerAuth")
public class MinhaContaController {

    private final MinhaContaService minhaContaService;

    @GetMapping
    @Operation(
            summary = "Consultar minha conta",
            description = "Retorna os dados pessoais do cliente autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<MinhaContaGetResponseDTO> find() {
        return ResponseEntity.status(HttpStatus.OK).body(minhaContaService.find());
    }

    @PutMapping
    @Operation(
            summary = "Atualizar minha conta",
            description = "Atualiza os dados pessoais do cliente autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<MinhaContaGetResponseDTO> update(@Valid @RequestBody MinhaContaPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(minhaContaService.update(dto));
    }
}