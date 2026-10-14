package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.LoginPostRequestDTO;
import br.com.bw.backend.dto.response.AuthGetResponseDTO;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.exception.UnauthorizedException;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final String MENSAGEM_ERRO = "E-mail ou senha inválidos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthGetResponseDTO login(LoginPostRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();
        log.info("Tentativa de login - email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        String hash = usuario != null ? usuario.getSenhaHash() : HASH_FALSO;
        boolean senhaOk = passwordEncoder.matches(dto.senha(), hash);

        if (usuario == null || !senhaOk || !Boolean.TRUE.equals(usuario.getAtivo())) {
            log.warn("Login negado - email: {}", email);
            throw new UnauthorizedException(MENSAGEM_ERRO);
        }

        String token = jwtService.gerar(usuario.getId(), usuario.getEmail());
        log.info("Login realizado com sucesso - id: {}", usuario.getId());

        return new AuthGetResponseDTO(
                token,
                "Bearer",
                jwtService.getExpiracaoSegundos(),
                usuario.getId(),
                usuario.getNome());
    }

    private static final String HASH_FALSO =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoOa1v3N6Q9u2Zb1x0J8mY4kLw5eDq3sVa";
}