package br.com.bw.backend.controller;

import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.service.EstadoOAuthGoogleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Google Calendar", description = "Fluxo de autorização OAuth2 para conectar o Google Calendar (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class GoogleCalendarOAuthController {

    private static final String URL_AUTORIZACAO_GOOGLE =
            "https://accounts.google.com/o/oauth2/v2/auth";

    private static final String COOKIE_VINCULO_NAVEGADOR =
            "google_oauth_browser";

    private static final int VALIDADE_MINUTOS = 5;

    private final EstadoOAuthGoogleService estadoOAuthGoogleService;
    private final GoogleCalendarProperties googleCalendarProperties;

    @PostMapping("/autorizar")
    @Operation(
            summary = "Iniciar autorização do Google Calendar",
            description = "Gera a URL de consentimento do Google OAuth2 que o administrador deve abrir no navegador "
                    + "para autorizar o acesso ao Google Calendar. O callback é recebido em GET /login/oauth2/code/google.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL de autorização gerada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<GoogleAuthorizationUrlResponse> authorize() {

        String estado = estadoOAuthGoogleService.generateRandomValue();
        String vinculoNavegador = estadoOAuthGoogleService.generateRandomValue();

        estadoOAuthGoogleService.createState(estado, vinculoNavegador);

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

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new GoogleAuthorizationUrlResponse(urlAutorizacao));
    }

    @Schema(description = "URL de consentimento do Google OAuth2 para ser aberta pelo administrador")
    public record GoogleAuthorizationUrlResponse(String authorizationUrl) {
    }
}

