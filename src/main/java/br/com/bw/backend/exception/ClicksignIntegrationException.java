package br.com.bw.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ClicksignIntegrationException extends RuntimeException {
    public ClicksignIntegrationException(String message) {
        super(message);
    }

    public ClicksignIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
