package br.com.bw.backend.controller;

import br.com.bw.backend.config.CredenciaisGoogleCalendarService;
import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.service.EstadoOAuthGoogleService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class GoogleCalendarOAuthCallbackController {

    private static final String COOKIE_VINCULO_NAVEGADOR =
            "google_oauth_browser";

    private final EstadoOAuthGoogleService estadoOAuthGoogleService;
    private final GoogleCalendarProperties googleCalendarProperties;
    private final CredenciaisGoogleCalendarService credenciaisService;

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @CookieValue(
                    name = COOKIE_VINCULO_NAVEGADOR,
                    required = false
            ) String vinculoNavegador,
            HttpServletResponse servletResponse
    ) {
        if (state == null || vinculoNavegador == null
                || !estadoOAuthGoogleService.consumirEstado(
                state, vinculoNavegador)) {
            return resposta(
                    HttpStatus.BAD_REQUEST,
                    "Autorização inválida ou expirada.",
                    servletResponse
            );
        }

        if (error != null) {
            return resposta(
                    HttpStatus.BAD_REQUEST,
                    "A autorização do Google não foi concluída.",
                    servletResponse
            );
        }

        if (code == null || code.isBlank()) {
            return resposta(
                    HttpStatus.BAD_REQUEST,
                    "O Google não retornou um código de autorização.",
                    servletResponse
            );
        }

        try {
            GoogleTokenResponse tokens =
                    new GoogleAuthorizationCodeTokenRequest(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance(),
                            googleCalendarProperties.getClientId(),
                            googleCalendarProperties.getClientSecret(),
                            code,
                            googleCalendarProperties.getRedirectUri()
                    ).execute();

            String refreshToken = tokens.getRefreshToken();

            if (refreshToken == null || refreshToken.isBlank()) {
                return resposta(
                        HttpStatus.BAD_GATEWAY,
                        "O Google não forneceu um refresh token. "
                                + "Verifique a autorização concedida "
                                + "e tente conectar novamente.",
                        servletResponse
                );
            }

            String escopo = tokens.getScope() != null
                    ? tokens.getScope()
                    : googleCalendarProperties.getScope();

            credenciaisService.salvarOuAtualizar(refreshToken, escopo);

            return resposta(
                    HttpStatus.OK,
                    "Google Calendar conectado com sucesso. "
                            + "Você já pode fechar esta página.",
                    servletResponse
            );

        } catch (IOException | GeneralSecurityException e) {
            return resposta(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível concluir a conexão com o Google Calendar.",
                    servletResponse
            );
        }
    }

    private ResponseEntity<String> resposta(
            HttpStatus status,
            String mensagem,
            HttpServletResponse servletResponse
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_VINCULO_NAVEGADOR, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/login/oauth2/code/google")
                .maxAge(Duration.ZERO)
                .build();

        servletResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.status(status)
                .contentType(MediaType.TEXT_PLAIN)
                .body(mensagem);
    }
}

