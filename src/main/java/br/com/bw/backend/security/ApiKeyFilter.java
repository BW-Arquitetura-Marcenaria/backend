package br.com.bw.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Api-Key";
    private final byte[] chaveEsperada;

    public ApiKeyFilter(String apiKey) {
        this.chaveEsperada = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/v1/api/integracoes/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String enviada = request.getHeader(HEADER);

        if (enviada != null
                && MessageDigest.isEqual(chaveEsperada, enviada.getBytes(StandardCharsets.UTF_8))) {
            var auth = new UsernamePasswordAuthenticationToken(
                    "n8n", null, List.of(new SimpleGrantedAuthority("ROLE_INTEGRACAO")));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        }
        chain.doFilter(request, response);
    }
}