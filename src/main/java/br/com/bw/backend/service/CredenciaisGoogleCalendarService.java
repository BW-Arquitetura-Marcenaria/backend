package br.com.bw.backend.service;

import br.com.bw.backend.entity.CredenciaisGoogleCalendar;
import br.com.bw.backend.repository.CredenciaisGoogleCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CredenciaisGoogleCalendarService {

    private final CredenciaisGoogleCalendarRepository repository;
    private final GoogleTokenEncryptionService encryptionService;

    @Transactional
    public void saveOrUpdate(String refreshToken, String escopo) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException(
                    "O Google não retornou um refresh token válido."
            );
        }

        if (escopo == null || escopo.isBlank()) {
            throw new IllegalArgumentException(
                    "O escopo autorizado não pode ser vazio."
            );
        }

        CredenciaisGoogleCalendar credenciais = repository
                .findFirstByOrderByIdAsc()
                .orElseGet(CredenciaisGoogleCalendar::new);

        credenciais.setTokenAtualizacaoCriptografado(
                encryptionService.encrypt(refreshToken)
        );

        credenciais.setEscopo(escopo);

        repository.save(credenciais);
    }

    @Transactional(readOnly = true)
    public String getRefreshToken() {
        CredenciaisGoogleCalendar credenciais = repository
                .findFirstByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException(
                        "O Google Calendar ainda não foi conectado."
                ));

        return encryptionService.decrypt(
                credenciais.getTokenAtualizacaoCriptografado()
        );
    }

    @Transactional(readOnly = true)
    public Optional<String> getSyncToken() {
        return repository.findFirstByOrderByIdAsc()
                .map(CredenciaisGoogleCalendar::getSyncToken);
    }

    @Transactional
    public void saveSyncToken(String syncToken) {
        repository.findFirstByOrderByIdAsc().ifPresent(credenciais -> {
            credenciais.setSyncToken(syncToken);
            repository.save(credenciais);
        });
    }
}
