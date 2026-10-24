package br.com.bw.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class GoogleCalendarIntegrationException extends RuntimeException {
    public GoogleCalendarIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
