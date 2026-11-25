package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.LeadInstagramPostRequestDTO;
import br.com.bw.backend.dto.response.LeadInstagramPostResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.exception.BusinessValidationException;
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
public class LeadInstagramService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Value("${app.cadastro.base-url}")
    private String baseUrl;

    @Value("${app.cadastro.token-validade-horas:48}")
    private long validadeHoras;

    @Transactional
    public LeadInstagramPostResponseDTO register(LeadInstagramPostRequestDTO dto) {
        if (dto.usuarioId() != null) {
            return registerPlatform(dto);
        }
        if (!hasText(dto.instagramId())) {
            throw new BusinessValidationException("Informe instagramId ou usuarioId");
        }
        return registerInstagram(dto);
    }

    private LeadInstagramPostResponseDTO registerPlatform(LeadInstagramPostRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new BusinessValidationException("Usuário não encontrado"));

        LeadInstagram lead = leadRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    LeadInstagram novo = new LeadInstagram();
                    novo.setOrigem(LeadInstagram.ORIGEM_PLATAFORMA);
                    novo.setUsuario(usuario);
                    novo.setEmail(usuario.getEmail());
                    return novo;
                });

        updateData(lead, dto);
        leadRepository.save(lead);

        EtapaFunil alvo = Boolean.TRUE.equals(lead.getSaoPaulo())
                ? EtapaFunil.QUALIFICADO
                : EtapaFunil.EM_QUALIFICACAO;
        EtapaFunil atual = usuario.getEtapaFunil();
        if (atual == null || atual.ordinal() < alvo.ordinal()) {
            usuario.setEtapaFunil(alvo);
        }

        log.info("Lead da plataforma atualizado - usuarioId: {}, etapa: {}",
                usuario.getId(), usuario.getEtapaFunil());
        return new LeadInstagramPostResponseDTO(true, false, null);
    }

    private LeadInstagramPostResponseDTO registerInstagram(LeadInstagramPostRequestDTO dto) {
        if (usuarioRepository.existsByInstagramId(dto.instagramId())) {
            log.info("Lead já cadastrado como usuário - instagramId: {}", dto.instagramId());
            return new LeadInstagramPostResponseDTO(true, false, null);
        }

        LeadInstagram lead = leadRepository.findByInstagramId(dto.instagramId())
                .orElseGet(() -> {
                    LeadInstagram novo = new LeadInstagram();
                    novo.setInstagramId(dto.instagramId());
                    novo.setOrigem(LeadInstagram.ORIGEM_INSTAGRAM);
                    return novo;
                });

        updateData(lead, dto);

        LocalDateTime agora = LocalDateTime.now();
        boolean tokenValido = lead.getTokenHash() != null
                && lead.getTokenUsadoEm() == null
                && lead.getTokenExpiraEm() != null
                && lead.getTokenExpiraEm().isAfter(agora);

        if (tokenValido) {
            leadRepository.save(lead);
            log.info("Lead atualizado com token ainda válido - instagramId: {}", dto.instagramId());
            return new LeadInstagramPostResponseDTO(false, true, null);
        }

        String token = tokenService.generate();
        lead.setTokenHash(tokenService.hash(token));
        lead.setTokenExpiraEm(agora.plusHours(validadeHoras));
        lead.setTokenUsadoEm(null);
        lead.setLinkEnviadoEm(agora);
        leadRepository.save(lead);

        log.info("Novo link de cadastro gerado para lead - instagramId: {}", dto.instagramId());
        return new LeadInstagramPostResponseDTO(false, false, baseUrl + "/cadastro?token=" + token);
    }

    private void updateData(LeadInstagram lead, LeadInstagramPostRequestDTO dto) {
        if (hasText(dto.crmLeadId())) lead.setCrmLeadId(dto.crmLeadId());
        if (hasText(dto.nome())) lead.setNome(dto.nome());
        if (dto.saoPaulo() != null) lead.setSaoPaulo(dto.saoPaulo());
        if (hasText(dto.ambiente())) lead.setAmbiente(dto.ambiente());
        if (hasText(dto.movel())) lead.setMovel(dto.movel());
        if (hasText(dto.cor())) lead.setCor(dto.cor());
        if (hasText(dto.material())) lead.setMaterial(dto.material());
        if (dto.orcamento() != null && dto.orcamento().signum() > 0) lead.setOrcamento(dto.orcamento());
        if (hasText(dto.situacaoProjeto())) lead.setSituacaoProjeto(dto.situacaoProjeto());
        if (dto.precisaVisitaTecnica() != null) lead.setPrecisaVisitaTecnica(dto.precisaVisitaTecnica());
        if (hasText(dto.resumoConversa())) lead.setResumoConversa(dto.resumoConversa());
        if (dto.score() != null) lead.setScore(dto.score());
        if (hasText(dto.classificacao())) lead.setClassificacao(dto.classificacao().trim());
    }

    private boolean hasText(String valor) {
        return valor != null && !valor.isBlank();
    }
}