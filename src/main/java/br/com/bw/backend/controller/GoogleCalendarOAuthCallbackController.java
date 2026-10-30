package br.com.bw.backend.controller;

import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.service.CredenciaisGoogleCalendarService;
import br.com.bw.backend.service.EstadoOAuthGoogleService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Google Calendar", description = "Callback público do OAuth2 do Google (chamado pelo próprio Google, não pelo front-end)")
public class GoogleCalendarOAuthCallbackController {

    private static final String COOKIE_VINCULO_NAVEGADOR =
            "google_oauth_browser";

    private final EstadoOAuthGoogleService estadoOAuthGoogleService;
    private final GoogleCalendarProperties googleCalendarProperties;
    private final CredenciaisGoogleCalendarService credenciaisService;

    @GetMapping("/login/oauth2/code/google")
    @Hidden
    @Operation(
            summary = "Callback de autorização do Google",
            description = "Endpoint público chamado pelo Google após o consentimento do usuário. "
                    + "Troca o código de autorização pelo refresh token e salva as credenciais da empresa. "
                    + "Não deve ser chamado manualmente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Google Calendar conectado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Autorização inválida, expirada ou negada pelo usuário"),
            @ApiResponse(responseCode = "502", description = "Falha ao trocar o código de autorização com o Google")
    })
    public ResponseEntity<String> callback(
            @Parameter(description = "Código de autorização retornado pelo Google")
            @RequestParam(required = false) String code,
            @Parameter(description = "Valor de estado (CSRF) gerado em /autorizar")
            @RequestParam(required = false) String state,
            @Parameter(description = "Código de erro retornado pelo Google, quando a autorização é negada")
            @RequestParam(required = false) String error,
            @CookieValue(
                    name = COOKIE_VINCULO_NAVEGADOR,
                    required = false
            ) String vinculoNavegador,
            HttpServletResponse servletResponse
    ) {
        if (state == null || vinculoNavegador == null
                || !estadoOAuthGoogleService.consumeState(
                state, vinculoNavegador)) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "Autorização inválida ou expirada.",
                    servletResponse
            );
        }

        if (error != null) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "A autorização do Google não foi concluída.",
                    servletResponse
            );
        }

        if (code == null || code.isBlank()) {
            return buildResponse(
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
                return buildResponse(
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

            credenciaisService.saveOrUpdate(refreshToken, escopo);

            return buildResponse(
                    HttpStatus.OK,
                    "Google Calendar conectado com sucesso. "
                            + "Você já pode fechar esta página.",
                    servletResponse
            );

        } catch (IOException | GeneralSecurityException e) {
            return buildResponse(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível concluir a conexão com o Google Calendar.",
                    servletResponse
            );
        }
    }

    private ResponseEntity<String> buildResponse(
            HttpStatus status,
            String mensagem,
            HttpServletResponse servletResponse
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_VINCULO_NAVEGADOR, "")
                .httpOnly(true)
                .secure(googleCalendarProperties.isCookieSecure())
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

