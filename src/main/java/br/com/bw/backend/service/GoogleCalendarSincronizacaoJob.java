package br.com.bw.backend.service;

import br.com.bw.backend.dto.response.GoogleCalendarSincronizacaoGetResponseDTO;
import br.com.bw.backend.exception.GoogleCalendarDesconectadoException;
import br.com.bw.backend.exception.GoogleCalendarSyncTokenInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarSincronizacaoJob {

    private final GoogleCalendarService googleCalendarService;
    private final CredenciaisGoogleCalendarService credenciaisService;
    private final AgendamentoService agendamentoService;

    @Scheduled(
            fixedDelayString = "${integracoes.google.calendar.sincronizacao.intervalo-ms:300000}",
            initialDelayString = "${integracoes.google.calendar.sincronizacao.atraso-inicial-ms:30000}"
    )
    public void synchronize() {
        try {
            String syncTokenAtual = credenciaisService.getSyncToken().orElse(null);

            GoogleCalendarSincronizacaoGetResponseDTO resultado;
            try {
                resultado = googleCalendarService.synchronizeChanges(syncTokenAtual);
            } catch (GoogleCalendarSyncTokenInvalidoException e) {
                log.warn("{} Reiniciando sincronização completa.", e.getMessage());
                credenciaisService.saveSyncToken(null);
                resultado = googleCalendarService.synchronizeChanges(null);
            }

            resultado.eventos().forEach(agendamentoService::reconcileWithGoogle);

            credenciaisService.saveSyncToken(resultado.proximoSyncToken());

            if (!resultado.eventos().isEmpty()) {
                log.info(
                        "Sincronização com o Google Calendar processou {} alteração(ões).",
                        resultado.eventos().size()
                );
            }
        } catch (GoogleCalendarDesconectadoException e) {
            log.debug("Sincronização com o Google Calendar ignorada: conexão não configurada.");
        } catch (Exception e) {
            log.error("Falha ao sincronizar alterações do Google Calendar.", e);
        }
    }
}
