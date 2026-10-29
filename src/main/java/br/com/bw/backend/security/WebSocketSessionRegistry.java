package br.com.bw.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketSessionRegistry {

    private static final long TOLERANCIA_SEM_CONNECT_MS = 30_000;

    private static class Conexao {
        private final WebSocketSession session;
        private final long abertaEm = System.currentTimeMillis();
        private volatile Long expiraEm;

        private Conexao(WebSocketSession session) {
            this.session = session;
        }
    }

    private final Map<String, Conexao> conexoes = new ConcurrentHashMap<>();

    public void registrar(WebSocketSession session) {
        conexoes.put(session.getId(), new Conexao(session));
    }

    public void remover(String sessionId) {
        conexoes.remove(sessionId);
    }

    public void definirExpiracao(String sessionId, Date expiraEm) {
        Conexao conexao = conexoes.get(sessionId);
        if (conexao != null && expiraEm != null) {
            conexao.expiraEm = expiraEm.getTime();
        }
    }

    @Scheduled(fixedDelay = 30_000)
    public void derrubarVencidas() {
        long agora = System.currentTimeMillis();

        conexoes.forEach((id, conexao) -> {
            boolean vencida = conexao.expiraEm != null
                    ? agora > conexao.expiraEm
                    : agora - conexao.abertaEm > TOLERANCIA_SEM_CONNECT_MS;

            if (vencida) {
                conexoes.remove(id);
                try {
                    conexao.session.close(CloseStatus.POLICY_VIOLATION.withReason("Sessão expirada"));
                    log.info("Conexão WebSocket encerrada por expiração - sessão: {}", id);
                } catch (Exception e) {
                    log.warn("Falha ao encerrar conexão WebSocket {}", id, e);
                }
            }
        });
    }
}
