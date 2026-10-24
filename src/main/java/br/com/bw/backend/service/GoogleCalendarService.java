package br.com.bw.backend.service;

import br.com.bw.backend.config.CredenciaisGoogleCalendarService;
import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.dto.request.CriarEventoGoogleRequest;
import br.com.bw.backend.dto.response.EventoGoogleResponse;
import br.com.bw.backend.dto.response.GoogleCalendarResponse;
import br.com.bw.backend.exception.BusinessValidationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

    public List<GoogleCalendarResponse> listarCalendarios()
            throws IOException, GeneralSecurityException {

        Calendar cliente = obterCliente();

        CalendarList calendarios = cliente.calendarList()
                .list()
                .execute();

        return calendarios.getItems()
                .stream()
                .map(calendario -> new GoogleCalendarResponse(
                        calendario.getId(),
                        calendario.getSummary(),
                        calendario.getTimeZone(),
                        Boolean.TRUE.equals(calendario.getPrimary())
                ))
                .toList();
    }


    public EventoGoogleResponse criarEvento(CriarEventoGoogleRequest request)
            throws IOException, GeneralSecurityException {

        if (request == null
                || request.titulo() == null
                || request.titulo().isBlank()
                || request.inicio() == null
                || request.fim() == null) {
            throw new BusinessValidationException(
                    "Título, início e fim são obrigatórios."
            );
        }

        LocalDateTime inicio = request.inicio();
        LocalDateTime fim = request.fim();

        if (!fim.isAfter(inicio)) {
            throw new BusinessValidationException(
                    "O fim do evento deve ser posterior ao início."
            );
        }

        ZoneId fuso = ZoneId.of("America/Sao_Paulo");

        Event evento = new Event()
                .setSummary(request.titulo().trim())
                .setDescription(request.descricao());

        evento.setStart(new EventDateTime()
                .setDateTime(new DateTime(
                        Date.from(inicio.atZone(fuso).toInstant())
                ))
                .setTimeZone(fuso.getId()));

        evento.setEnd(new EventDateTime()
                .setDateTime(new DateTime(
                        Date.from(fim.atZone(fuso).toInstant())
                ))
                .setTimeZone(fuso.getId()));

        Calendar cliente = obterCliente();

        Event criado = cliente.events()
                .insert("primary", evento)
                .execute();

        return new EventoGoogleResponse(
                criado.getId(),
                criado.getSummary(),
                criado.getHtmlLink()
        );
    }

}

