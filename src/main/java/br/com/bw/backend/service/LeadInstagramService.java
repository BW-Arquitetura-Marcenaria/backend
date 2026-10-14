package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.LeadInstagramPostRequestDTO;
import br.com.bw.backend.dto.response.LeadInstagramPostResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeadInstagramService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Value("${app.cadastro.base-url}")
    private String baseUrl;

    @Value("${app.cadastro.token-validade-horas:48}")
    private long validadeHoras;

    @Transactional
    public LeadInstagramPostResponseDTO registrar(LeadInstagramPostRequestDTO dto) {
        if (usuarioRepository.existsByInstagramId(dto.instagramId())) {
            log.info("Lead já cadastrado como usuário - instagramId: {}", dto.instagramId());
            return new LeadInstagramPostResponseDTO(true, false, null);
        }

        LeadInstagram lead = leadRepository.findByInstagramId(dto.instagramId())
                .orElseGet(() -> {
                    LeadInstagram novo = new LeadInstagram();
                    novo.setInstagramId(dto.instagramId());
                    return novo;
                });

        atualizarDados(lead, dto);

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

        String token = tokenService.gerar();
        lead.setTokenHash(tokenService.hash(token));
        lead.setTokenExpiraEm(agora.plusHours(validadeHoras));
        lead.setTokenUsadoEm(null);
        lead.setLinkEnviadoEm(agora);
        leadRepository.save(lead);

        log.info("Novo link de cadastro gerado para lead - instagramId: {}", dto.instagramId());
        return new LeadInstagramPostResponseDTO(false, false, baseUrl + "/cadastro?token=" + token);
    }

    private void atualizarDados(LeadInstagram lead, LeadInstagramPostRequestDTO dto) {
        if (temTexto(dto.crmLeadId())) lead.setCrmLeadId(dto.crmLeadId());
        if (temTexto(dto.nome())) lead.setNome(dto.nome());
        if (dto.saoPaulo() != null) lead.setSaoPaulo(dto.saoPaulo());
        if (temTexto(dto.ambiente())) lead.setAmbiente(dto.ambiente());
        if (temTexto(dto.movel())) lead.setMovel(dto.movel());
        if (temTexto(dto.cor())) lead.setCor(dto.cor());
        if (temTexto(dto.material())) lead.setMaterial(dto.material());
        if (dto.orcamento() != null && dto.orcamento().signum() > 0) lead.setOrcamento(dto.orcamento());
        if (temTexto(dto.situacaoProjeto())) lead.setSituacaoProjeto(dto.situacaoProjeto());
        if (dto.precisaVisitaTecnica() != null) lead.setPrecisaVisitaTecnica(dto.precisaVisitaTecnica());
        if (temTexto(dto.resumoConversa())) lead.setResumoConversa(dto.resumoConversa());
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}