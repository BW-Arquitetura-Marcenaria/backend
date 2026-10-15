package br.com.bw.backend.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public final class UsuarioLogado {

    private UsuarioLogado() {}

    public static Integer id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer id) {
            return id;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}