package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.CriarEventoGoogleRequest;
import br.com.bw.backend.dto.response.EventoGoogleResponse;
import br.com.bw.backend.dto.response.GoogleCalendarResponse;
import br.com.bw.backend.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/calendarios")
    public List<GoogleCalendarResponse> listarCalendarios()
            throws IOException, GeneralSecurityException {

        return googleCalendarService.listarCalendarios();
    }

    @PostMapping("/eventos")
    public ResponseEntity<EventoGoogleResponse> criarEvento(
            @RequestBody CriarEventoGoogleRequest request
    ) throws IOException, GeneralSecurityException {

        EventoGoogleResponse resposta =
                googleCalendarService.criarEvento(request);

        return ResponseEntity.status(201).body(resposta);
    }
}
