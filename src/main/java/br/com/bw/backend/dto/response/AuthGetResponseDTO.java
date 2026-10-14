package br.com.bw.backend.dto.response;

public record AuthGetResponseDTO(
        String token,
        String tipo,
        long expiraEmSegundos,
        Integer usuarioId,
        String nome
) {}