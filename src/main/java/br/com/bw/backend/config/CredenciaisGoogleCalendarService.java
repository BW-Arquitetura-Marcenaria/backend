package br.com.bw.backend.config;

import br.com.bw.backend.entity.CredenciaisGoogleCalendar;
import br.com.bw.backend.repository.CredenciaisGoogleCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredenciaisGoogleCalendarService {

    private final CredenciaisGoogleCalendarRepository repository;
    private final GoogleTokenEncryptionService encryptionService;

    @Transactional
    public void salvarOuAtualizar(String refreshToken, String escopo) {

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
                encryptionService.criptografar(refreshToken)
        );

        credenciais.setEscopo(escopo);

        repository.save(credenciais);
    }

    @Transactional(readOnly = true)
    public String obterRefreshToken() {
        CredenciaisGoogleCalendar credenciais = repository
                .findFirstByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException(
                        "O Google Calendar ainda não foi conectado."
                ));

        return encryptionService.descriptografar(
                credenciais.getTokenAtualizacaoCriptografado()
        );
    }
}

