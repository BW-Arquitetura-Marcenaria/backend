package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.LoginPostRequestDTO;
import br.com.bw.backend.dto.response.AuthGetResponseDTO;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MENSAGEM_ERRO = "E-mail ou senha inválidos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthGetResponseDTO login(LoginPostRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email().trim().toLowerCase())
                .orElse(null);

        String hash = usuario != null ? usuario.getSenhaHash() : HASH_FALSO;
        boolean senhaOk = passwordEncoder.matches(dto.senha(), hash);

        if (usuario == null || !senhaOk || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAGEM_ERRO);
        }

        String token = jwtService.gerar(usuario.getId(), usuario.getEmail(), usuario.getPerfil());

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