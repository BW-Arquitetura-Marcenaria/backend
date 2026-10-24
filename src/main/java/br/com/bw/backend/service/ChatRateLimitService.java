package br.com.bw.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRateLimitService {

    private static final long JANELA_MS = 60_000;

    private final int limitePorMinuto;
    private final Map<Integer, Deque<Long>> envios = new ConcurrentHashMap<>();

    public ChatRateLimitService(@Value("${app.chat.limite-mensagens-por-minuto:20}") int limitePorMinuto) {
        this.limitePorMinuto = limitePorMinuto;
    }

    public void validar(Integer usuarioId) {
        long agora = System.currentTimeMillis();
        Deque<Long> fila = envios.computeIfAbsent(usuarioId, id -> new ArrayDeque<>());

        synchronized (fila) {
            while (!fila.isEmpty() && fila.peekFirst() < agora - JANELA_MS) {
                fila.pollFirst();
            }
            if (fila.size() >= limitePorMinuto) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Muitas mensagens em pouco tempo. Aguarde um instante.");
            }
            fila.addLast(agora);
        }
    }
}