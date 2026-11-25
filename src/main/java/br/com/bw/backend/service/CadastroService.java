package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.CadastroDiretoPostRequestDTO;
import br.com.bw.backend.dto.request.CadastroPostRequestDTO;
import br.com.bw.backend.dto.response.CadastroGetResponseDTO;
import br.com.bw.backend.dto.response.CadastroValidarGetResponseDTO;
import br.com.bw.backend.entity.Empresa;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.TipoCliente;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.ConflictException;
import br.com.bw.backend.mapper.CadastroMapper;
import br.com.bw.backend.repository.ChatbotLogRepository;
import br.com.bw.backend.repository.EmpresaRepository;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CadastroService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ChatbotLogRepository chatbotLogRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CadastroMapper mapper;

    @Transactional(readOnly = true)
    public CadastroValidarGetResponseDTO validate(String token) {
        return mapper.toValidateResponse(findValidLead(token));
    }

    @Transactional
    public CadastroGetResponseDTO register(CadastroPostRequestDTO dto) {
        LeadInstagram lead = findValidLead(dto.token());

        String email = normalizeEmail(dto.email());
        String instagramId = lead.getInstagramId();
        boolean veioDoInstagram = !isBlank(instagramId);

        if (!isBlank(lead.getEmail()) && !lead.getEmail().equalsIgnoreCase(email)) {
            log.warn("Cadastro negado - e-mail diferente do convite: {}", email);
            throw new BusinessValidationException("O e-mail deve ser o mesmo do convite");
        }
        if (leadRepository.findByEmail(email)
                .filter(outro -> !outro.getId().equals(lead.getId()))
                .isPresent()) {
            log.warn("Cadastro negado - e-mail pertence a outro lead: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }
        if (veioDoInstagram && usuarioRepository.existsByInstagramId(instagramId)) {
            log.warn("Cadastro negado - conta já cadastrada: {}", instagramId);
            throw new ConflictException("Conta já cadastrada");
        }

        Usuario usuario = createUser(dto, email,
                veioDoInstagram ? instagramId : null,
                defineInitialStage(lead, veioDoInstagram));

        if (veioDoInstagram) {
            chatbotLogRepository.vincularUsuario(instagramId, usuario);
        }

        lead.setUsuario(usuario);
        lead.setEmail(email);
        lead.setTokenUsadoEm(LocalDateTime.now());
        leadRepository.save(lead);

        log.info("Cadastro realizado com sucesso - id: {}", usuario.getId());
        return mapper.toResponse(usuario);
    }

    /**
     * Cadastro de quem entrou direto pela plataforma, sem link. Nasce em Novo Lead,
     * pois a IA ainda não falou com ele.
     */
    @Transactional
    public CadastroGetResponseDTO registerDirect(CadastroDiretoPostRequestDTO form) {
        String email = normalizeEmail(form.email());

        CadastroPostRequestDTO dto = new CadastroPostRequestDTO(
                null,
                form.tipoCliente(),
                form.nome(),
                form.sobrenome(),
                form.cpf(),
                form.dataNascimento(),
                form.email(),
                form.telefone(),
                form.origemContato(),
                form.senha(),
                form.cnpj(),
                form.razaoSocial(),
                form.nomeFantasia());

        Usuario usuario = createUser(dto, email, null, EtapaFunil.NOVO_LEAD);

        leadRepository.findByEmail(email)
                .filter(lead -> lead.getUsuario() == null)
                .ifPresent(lead -> {
                    lead.setUsuario(usuario);
                    lead.setTokenUsadoEm(LocalDateTime.now());
                    leadRepository.save(lead);
                    log.info("Convite pendente vinculado ao cadastro direto - lead: {}", lead.getId());
                });

        log.info("Cadastro direto realizado com sucesso - id: {}", usuario.getId());
        return mapper.toResponse(usuario);
    }

    private Usuario createUser(CadastroPostRequestDTO dto, String email, String instagramId, EtapaFunil etapa) {
        boolean pessoaJuridica = dto.tipoCliente() == TipoCliente.PESSOA_JURIDICA;
        String cnpj = onlyDigits(dto.cnpj());

        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Cadastro negado - e-mail já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }
        if (pessoaJuridica) {
            if (cnpj.isEmpty()) {
                log.warn("Cadastro negado - CNPJ obrigatório não informado: {}", email);
                throw new BusinessValidationException("CNPJ é obrigatório");
            }
            if (isBlank(dto.razaoSocial())) {
                log.warn("Cadastro negado - razão social não informada: {}", email);
                throw new BusinessValidationException("Razão social é obrigatória");
            }
            if (empresaRepository.existsByCnpj(cnpj)) {
                log.warn("Cadastro negado - CNPJ já cadastrado: {}", cnpj);
                throw new ConflictException("CNPJ já cadastrado");
            }
        }

        Usuario usuario = mapper.toEntity(dto);
        usuario.setEmail(email);
        usuario.setCpf(onlyDigits(dto.cpf()));
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setInstagramId(instagramId);
        usuario.setEtapaFunil(etapa);

        if (pessoaJuridica) {
            Empresa empresa = empresaRepository.save(Empresa.builder()
                    .cnpj(cnpj)
                    .razaoSocial(dto.razaoSocial().trim())
                    .nomeFantasia(isBlank(dto.nomeFantasia()) ? null : dto.nomeFantasia().trim())
                    .telefone(dto.telefone().trim())
                    .build());
            usuario.setEmpresa(empresa);
            usuario.setTelefone(null);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Quem veio do Instagram já conversou com a IA: SP segue direto como qualificado,
     * os demais ficam em qualificação. Quem veio por convite ainda não falou com a IA.
     */
    private EtapaFunil defineInitialStage(LeadInstagram lead, boolean veioDoInstagram) {
        if (!veioDoInstagram) {
            return EtapaFunil.NOVO_LEAD;
        }
        return Boolean.TRUE.equals(lead.getSaoPaulo())
                ? EtapaFunil.QUALIFICADO
                : EtapaFunil.EM_QUALIFICACAO;
    }

    private LeadInstagram findValidLead(String token) {
        LeadInstagram lead = leadRepository.findByTokenHash(tokenService.hash(token))
                .orElseThrow(this::invalidLink);

        if (lead.getTokenUsadoEm() != null
                || lead.getTokenExpiraEm() == null
                || lead.getTokenExpiraEm().isBefore(LocalDateTime.now())) {
            throw invalidLink();
        }
        return lead;
    }

    private ResponseStatusException invalidLink() {
        log.warn("Link de cadastro inválido ou expirado");
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String onlyDigits(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}