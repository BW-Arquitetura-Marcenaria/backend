package br.com.bw.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class GoogleCalendarDesconectadoException extends RuntimeException {
    public GoogleCalendarDesconectadoException(String message) {
        super(message);
    }
}
