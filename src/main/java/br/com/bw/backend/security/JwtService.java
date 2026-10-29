package br.com.bw.backend.security;

import br.com.bw.backend.entity.enums.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    public record JwtPayload(Integer usuarioId, Perfil perfil, Date expiraEm) {}

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(Integer usuarioId, String email, Perfil perfil) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiracaoMinutos * 60_000);

        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("email", email)
                .claim("perfil", perfil.name())
                .issuedAt(agora)
                .expiration(expira)
                .signWith(chave)
                .compact();
    }

    public Optional<JwtPayload> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String perfil = claims.get("perfil", String.class);
            if (perfil == null) {
                return Optional.empty();
            }

            return Optional.of(new JwtPayload(
                    Integer.valueOf(claims.getSubject()),
                    Perfil.valueOf(perfil),
                    claims.getExpiration()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpiracaoSegundos() {
        return expiracaoMinutos * 60;
    }
}
