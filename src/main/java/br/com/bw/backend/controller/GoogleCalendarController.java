package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.GoogleCalendarGetResponseDTO;
import br.com.bw.backend.service.GoogleCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/google-calendar")
@RequiredArgsConstructor
@Tag(name = "Google Calendar", description = "Consulta de calendários do Google Calendar conectado (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/calendarios")
    @Operation(
            summary = "Listar calendários do Google conectado",
            description = "Retorna os calendários disponíveis na conta Google autorizada para a empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calendários retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "502", description = "Falha ao se comunicar com a API do Google Calendar")
    })
    public List<GoogleCalendarGetResponseDTO> listCalendars()
            throws IOException, GeneralSecurityException {

        return googleCalendarService.listCalendars();
    }
}
