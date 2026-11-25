package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.ClienteConvitePostRequestDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.exception.ConflictException;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteConviteService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Value("${app.cadastro.base-url}")
    private String baseUrl;

    @Value("${app.cadastro.token-validade-horas:48}")
    private long validadeHoras;

    @Transactional
    public void invite(Integer adminId, ClienteConvitePostRequestDTO dto) {
        String nomeCompleto = dto.nomeCompleto().trim().replaceAll("\\s+", " ");
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Convite de cliente negado - e-mail já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }

        LeadInstagram lead = leadRepository.findByEmail(email).orElse(null);
        if (lead != null && lead.getUsuario() != null) {
            log.warn("Convite de cliente negado - lead já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }
        if (lead == null) {
            lead = new LeadInstagram();
            lead.setOrigem(LeadInstagram.ORIGEM_PLATAFORMA);
            lead.setEmail(email);
        }

        lead.setNome(nomeCompleto);
        if (hasText(dto.tipoProjeto())) {
            lead.setMovel(dto.tipoProjeto().trim());
        }
        if (dto.valor() != null && dto.valor().signum() > 0) {
            lead.setOrcamento(dto.valor());
        }

        String token = tokenService.generate();
        LocalDateTime agora = LocalDateTime.now();
        lead.setTokenHash(tokenService.hash(token));
        lead.setTokenExpiraEm(agora.plusHours(validadeHoras));
        lead.setTokenUsadoEm(null);
        lead.setLinkEnviadoEm(agora);
        leadRepository.save(lead);

        emailService.sendClientInvite(email, nomeCompleto, baseUrl + "/cadastro?token=" + token, validadeHoras);

        log.info("Convite de cliente enviado - lead: {} por: {}", lead.getId(), adminId);
    }

    private boolean hasText(String valor) {
        return valor != null && !valor.isBlank();
    }
}