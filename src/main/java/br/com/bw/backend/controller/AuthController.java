package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.LoginPostRequestDTO;
import br.com.bw.backend.dto.response.AuthGetResponseDTO;
import br.com.bw.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e emissão de token JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Valida e-mail e senha e retorna um token JWT para uso nos demais endpoints da API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos, ou usuário inativo")
    })
    public ResponseEntity<AuthGetResponseDTO> login(@Valid @RequestBody LoginPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
    }
}