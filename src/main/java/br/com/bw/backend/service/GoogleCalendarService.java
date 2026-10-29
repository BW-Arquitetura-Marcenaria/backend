package br.com.bw.backend.service;

import br.com.bw.backend.config.GoogleCalendarProperties;
import br.com.bw.backend.dto.request.GoogleCalendarEventoPostRequestDTO;
import br.com.bw.backend.dto.response.GoogleCalendarEventoAlteradoGetResponseDTO;
import br.com.bw.backend.dto.response.GoogleCalendarEventoPostResponseDTO;
import br.com.bw.backend.dto.response.GoogleCalendarGetResponseDTO;
import br.com.bw.backend.dto.response.GoogleCalendarSincronizacaoGetResponseDTO;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.GoogleCalendarDesconectadoException;
import br.com.bw.backend.exception.GoogleCalendarSyncTokenInvalidoException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private static final String MENSAGEM_DESCONECTADO =
            "A conexão com o Google Calendar expirou ou foi revogada. "
                    + "Reconecte em POST /v1/api/admin/google-calendar/autorizar.";

    private final GoogleCalendarProperties properties;
    private final CredenciaisGoogleCalendarService credenciaisService;

    public Calendar getClient() throws IOException, GeneralSecurityException {

        String refreshToken;

        try {
            refreshToken = credenciaisService.getRefreshToken();
        } catch (IllegalStateException e) {
            throw new GoogleCalendarDesconectadoException(MENSAGEM_DESCONECTADO);
        }

        UserCredentials credencial = UserCredentials.newBuilder()
                .setClientId(properties.getClientId())
                .setClientSecret(properties.getClientSecret())
                .setRefreshToken(refreshToken)
                .build();

        try {
            credencial.refresh();
        } catch (IOException e) {
            log.warn("Falha ao renovar token de acesso do Google Calendar: {}", e.getMessage());
            throw new GoogleCalendarDesconectadoException(MENSAGEM_DESCONECTADO);
        }

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credencial)
        )
                .setApplicationName("BW Arquitetura & Marcenaria")
                .build();
    }

    public List<GoogleCalendarGetResponseDTO> listCalendars()
            throws IOException, GeneralSecurityException {

        Calendar cliente = getClient();

        CalendarList calendarios = cliente.calendarList()
                .list()
                .execute();

        return calendarios.getItems()
                .stream()
                .map(calendario -> new GoogleCalendarGetResponseDTO(
                        calendario.getId(),
                        calendario.getSummary(),
                        calendario.getTimeZone(),
                        Boolean.TRUE.equals(calendario.getPrimary())
                ))
                .toList();
    }


    public GoogleCalendarEventoPostResponseDTO createEvent(GoogleCalendarEventoPostRequestDTO request)
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

        ZoneId fuso = ZoneId.of(properties.getTimezone());

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

        Calendar cliente = getClient();

        Event criado = cliente.events()
                .insert(properties.getCalendarId(), evento)
                .execute();

        return new GoogleCalendarEventoPostResponseDTO(
                criado.getId(),
                criado.getSummary(),
                criado.getHtmlLink()
        );
    }

    public void deleteEvent(String googleEventId) throws IOException, GeneralSecurityException {
        if (googleEventId == null || googleEventId.isBlank()) {
            return;
        }

        Calendar cliente = getClient();

        cliente.events()
                .delete(properties.getCalendarId(), googleEventId)
                .execute();
    }

    public GoogleCalendarSincronizacaoGetResponseDTO synchronizeChanges(String syncTokenAtual)
            throws IOException, GeneralSecurityException {

        Calendar cliente = getClient();

        List<GoogleCalendarEventoAlteradoGetResponseDTO> eventos = new ArrayList<>();
        String pageToken = null;
        String proximoSyncToken = null;

        do {
            Calendar.Events.List requisicao = cliente.events()
                    .list(properties.getCalendarId())
                    .setSingleEvents(true);

            if (syncTokenAtual != null) {
                requisicao.setSyncToken(syncTokenAtual);
            }

            if (pageToken != null) {
                requisicao.setPageToken(pageToken);
            }

            Events pagina;
            try {
                pagina = requisicao.execute();
            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() == 410) {
                    throw new GoogleCalendarSyncTokenInvalidoException(
                            "O syncToken do Google Calendar expirou; "
                                    + "é necessário reiniciar a sincronização completa."
                    );
                }
                throw e;
            }

            if (pagina.getItems() != null) {
                for (Event evento : pagina.getItems()) {
                    eventos.add(new GoogleCalendarEventoAlteradoGetResponseDTO(
                            evento.getId(),
                            evento.getStatus(),
                            convertToLocalDateTime(evento.getStart()),
                            convertToLocalDateTime(evento.getEnd())
                    ));
                }
            }

            pageToken = pagina.getNextPageToken();

            if (pagina.getNextSyncToken() != null) {
                proximoSyncToken = pagina.getNextSyncToken();
            }
        } while (pageToken != null);

        return new GoogleCalendarSincronizacaoGetResponseDTO(eventos, proximoSyncToken);
    }

    private LocalDateTime convertToLocalDateTime(EventDateTime dataEvento) {
        if (dataEvento == null) {
            return null;
        }

        DateTime dateTime = dataEvento.getDateTime() != null
                ? dataEvento.getDateTime()
                : dataEvento.getDate();

        if (dateTime == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateTime.getValue()),
                ZoneId.of(properties.getTimezone())
        );
    }

}

