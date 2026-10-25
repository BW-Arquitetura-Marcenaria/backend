package br.com.bw.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private static final String PREFIXO = "Bearer ";
    private static final String DESTINO_CLIENTE = "/user/queue/chat";
    private static final String DESTINO_EQUIPE = "/topic/admin/chat";

    private final JwtService jwtService;
    private final WebSocketSessionRegistry sessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand comando = accessor.getCommand();

        if (StompCommand.CONNECT.equals(comando)) {
            autenticar(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(comando)) {
            validarAssinatura(accessor);
        } else if (StompCommand.SEND.equals(comando)) {
            throw new MessagingException("Envio de mensagens pelo WebSocket não é permitido");
        }

        return message;
    }

    private void autenticar(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");

        if (header == null || !header.startsWith(PREFIXO)) {
            log.warn("Conexão WebSocket recusada - sem token");
            throw new MessagingException("Não autenticado");
        }

        String token = header.substring(PREFIXO.length()).trim();

        var payload = jwtService.validar(token)
                .orElseThrow(() -> {
                    log.warn("Conexão WebSocket recusada - token inválido ou expirado");
                    return new MessagingException("Não autenticado");
                });

        accessor.setUser(new UsernamePasswordAuthenticationToken(
                String.valueOf(payload.usuarioId()),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + payload.perfil().name()))));

        sessionRegistry.definirExpiracao(accessor.getSessionId(), payload.expiraEm());
    }

    private void validarAssinatura(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof Authentication usuario)) {
            throw new MessagingException("Não autenticado");
        }

        String destino = accessor.getDestination();

        if (DESTINO_CLIENTE.equals(destino) && possuiPerfil(usuario, "ROLE_CLIENTE")) {
            return;
        }
        if (DESTINO_EQUIPE.equals(destino) && possuiPerfil(usuario, "ROLE_ADMIN")) {
            return;
        }

        log.warn("Assinatura WebSocket negada - usuário: {} destino: {}", usuario.getName(), destino);
        throw new MessagingException("Acesso negado a este canal");
    }

    private boolean possuiPerfil(Authentication usuario, String perfil) {
        return usuario.getAuthorities().stream()
                .anyMatch(a -> perfil.equals(a.getAuthority()));
    }
}