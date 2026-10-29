package br.com.bw.backend.service;

import br.com.bw.backend.config.HashUtil;
import br.com.bw.backend.entity.EstadoOAuthGoogle;
import br.com.bw.backend.repository.EstadoOAuthGoogleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EstadoOAuthGoogleService {

    private static final int TAMANHO_BYTES = 32;
    private static final int VALIDADE_MINUTOS = 5;

    private final EstadoOAuthGoogleRepository repository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public EstadoOAuthGoogle createState(String estado, String vinculoNavegador) {
        EstadoOAuthGoogle registro = new EstadoOAuthGoogle();

        registro.setHashEstado(HashUtil.sha256(estado));
        registro.setHashVinculoNavegador(HashUtil.sha256(vinculoNavegador));
        registro.setDataExpiracao(
                LocalDateTime.now().plusMinutes(VALIDADE_MINUTOS)
        );

        return repository.save(registro);
    }

    public String generateRandomValue() {
        byte[] bytes = new byte[TAMANHO_BYTES];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Transactional
    public boolean consumeState(String estado, String vinculoNavegador) {

        if (estado == null || estado.isBlank()
                || vinculoNavegador == null || vinculoNavegador.isBlank()) {
            return false;
        }

        String hashEstado = HashUtil.sha256(estado);
        String hashVinculoNavegador = HashUtil.sha256(vinculoNavegador);

        LocalDateTime agora = LocalDateTime.now();

        int registrosAtualizados = repository.consumirSeValido(
                hashEstado,
                hashVinculoNavegador,
                agora,
                agora
        );

        return registrosAtualizados == 1;
    }
}