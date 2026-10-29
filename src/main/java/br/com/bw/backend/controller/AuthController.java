package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.LoginPostRequestDTO;
import br.com.bw.backend.dto.response.AuthGetResponseDTO;
import br.com.bw.backend.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthGetResponseDTO> login(@Valid @RequestBody LoginPostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
    }
}