package br.com.bw.backend.service;

import br.com.bw.backend.config.CredenciaisGoogleCalendarService;
import br.com.bw.backend.config.GoogleCalendarProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarProperties properties;
    private final CredenciaisGoogleCalendarService credenciaisService;

    public Calendar obterCliente() throws IOException, GeneralSecurityException {

        String refreshToken = credenciaisService.obterRefreshToken();

        GoogleCredential credencial = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(
                        properties.getClientId(),
                        properties.getClientSecret()
                )
                .build();

        credencial.setRefreshToken(refreshToken);

        boolean tokenRenovado = credencial.refreshToken();

        if (!tokenRenovado || credencial.getAccessToken() == null) {
            throw new IllegalStateException(
                    "Não foi possível renovar o token de acesso do Google."
            );
        }

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credencial
        )
                .setApplicationName("BW Arquitetura & Marcenaria")
                .build();
    }
}

