package br.com.bw.backend.service;

import br.com.bw.backend.config.CredenciaisGoogleCalendarService;
import br.com.bw.backend.dto.response.ResultadoSincronizacaoGoogle;
import br.com.bw.backend.exception.GoogleCalendarDesconectadoException;
import br.com.bw.backend.exception.GoogleCalendarSyncTokenInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleCalendarSincronizacaoJob {

    private final GoogleCalendarService googleCalendarService;
    private final CredenciaisGoogleCalendarService credenciaisService;
    private final AgendamentoService agendamentoService;

    @Scheduled(
            fixedDelayString = "${integracoes.google.calendar.sincronizacao.intervalo-ms:300000}",
            initialDelayString = "${integracoes.google.calendar.sincronizacao.atraso-inicial-ms:30000}"
    )
    public void sincronizar() {
        try {
            String syncTokenAtual = credenciaisService.obterSyncToken().orElse(null);

            ResultadoSincronizacaoGoogle resultado;
            try {
                resultado = googleCalendarService.sincronizarAlteracoes(syncTokenAtual);
            } catch (GoogleCalendarSyncTokenInvalidoException e) {
                log.warn("{} Reiniciando sincronização completa.", e.getMessage());
                credenciaisService.salvarSyncToken(null);
                resultado = googleCalendarService.sincronizarAlteracoes(null);
            }

            resultado.eventos().forEach(agendamentoService::reconciliarComGoogle);

            credenciaisService.salvarSyncToken(resultado.proximoSyncToken());

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
