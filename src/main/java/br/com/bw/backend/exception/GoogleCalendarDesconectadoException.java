package br.com.bw.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Lançada quando a conexão com o Google Calendar não pode ser renovada
 * (refresh token revogado, expirado ou nunca conectado), indicando que
 * é necessário refazer o fluxo de autorização em
 * {@code POST /v1/api/admin/google-calendar/autorizar}.
 */
@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class GoogleCalendarDesconectadoException extends RuntimeException {
    public GoogleCalendarDesconectadoException(String message) {
        super(message);
    }
}
