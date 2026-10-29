package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.GoogleCalendarGetResponseDTO;
import br.com.bw.backend.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
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
    public List<GoogleCalendarGetResponseDTO> listCalendars()
            throws IOException, GeneralSecurityException {

        return googleCalendarService.listCalendars();
    }
}
