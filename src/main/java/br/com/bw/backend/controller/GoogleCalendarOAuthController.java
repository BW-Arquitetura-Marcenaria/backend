package br.com.bw.backend.controller;

import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.service.EstadoOAuthGoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@RestController
@RequestMapping("/v1/api/admin/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarOAuthController {

    private static final String URL_AUTORIZACAO_GOOGLE =
            "https://accounts.google.com/o/oauth2/v2/auth";

    private static final String COOKIE_VINCULO_NAVEGADOR =
            "google_oauth_browser";

    private static final int VALIDADE_MINUTOS = 5;

    private final EstadoOAuthGoogleService estadoOAuthGoogleService;
    private final GoogleCalendarProperties googleCalendarProperties;

    @PostMapping("/autorizar")
    public ResponseEntity<GoogleAuthorizationUrlResponse> autorizar() {

        String estado = estadoOAuthGoogleService.gerarValorAleatorio();
        String vinculoNavegador = estadoOAuthGoogleService.gerarValorAleatorio();

        estadoOAuthGoogleService.criarEstado(estado, vinculoNavegador);

        String urlAutorizacao = UriComponentsBuilder
                .fromUriString(URL_AUTORIZACAO_GOOGLE)
                .queryParam("client_id", googleCalendarProperties.getClientId())
                .queryParam("redirect_uri", googleCalendarProperties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", googleCalendarProperties.getScope())
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", estado)
                .build()
                .encode()
                .toUriString();

        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_VINCULO_NAVEGADOR, vinculoNavegador)
                .httpOnly(true)
                .secure(googleCalendarProperties.isCookieSecure())
                .sameSite("Lax")
                .path("/login/oauth2/code/google")
                .maxAge(Duration.ofMinutes(VALIDADE_MINUTOS))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new GoogleAuthorizationUrlResponse(urlAutorizacao));
    }

    public record GoogleAuthorizationUrlResponse(String authorizationUrl) {
    }
}
