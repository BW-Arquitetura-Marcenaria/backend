package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.GoogleCalendarResponse;
import br.com.bw.backend.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarTesteController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/calendarios")
    public List<GoogleCalendarResponse> listarCalendarios()
            throws IOException, GeneralSecurityException {

        return googleCalendarService.listarCalendarios();
    }
}
