package br.com.bw.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandlerAdvice {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.warn("Recurso não encontrado - path: {}, message: {}", path, e.getMessage());

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(ConflictException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.warn("Conflito ao processar requisição - path: {}, message: {}", path, e.getMessage());

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidationException(BusinessValidationException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.warn("Erro de validação de negócio - path: {}, message: {}", path, e.getMessage());

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");

        var detalhes = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        log.warn("Erro de validação de campos - path: {}, detalhes: {}", path, detalhes);

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação",
                path,
                detalhes
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GoogleCalendarDesconectadoException.class)
    public ResponseEntity<ApiErrorResponse> handleGoogleCalendarDesconectadoException(GoogleCalendarDesconectadoException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.warn("Google Calendar desconectado - path: {}, message: {}", path, e.getMessage());

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FAILED_DEPENDENCY.value(),
                HttpStatus.FAILED_DEPENDENCY.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(error);
    }

    @ExceptionHandler(GoogleCalendarIntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleGoogleCalendarIntegrationException(GoogleCalendarIntegrationException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.error("Falha ao integrar com o Google Calendar - path: {}, message: {}", path, e.getMessage(), e);

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(UnauthorizedException e, WebRequest request) {
        var path = request.getDescription(false).replace("uri=", "");
        log.warn("Erro de validação de acesso - path: {}, message: {}", path, e.getMessage());

        var error = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                e.getMessage(),
                path,
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

}
