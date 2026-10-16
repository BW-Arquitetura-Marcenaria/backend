package br.com.bw.backend.service;

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
@Slf4j
@RequiredArgsConstructor
public class CadastroService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ChatbotLogRepository chatbotLogRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CadastroMapper mapper;

    @Transactional(readOnly = true)
    public CadastroValidarGetResponseDTO validar(String token) {
        return mapper.toValidarResponse(buscarLeadValido(token));
    }

    @Transactional
    public CadastroGetResponseDTO cadastrar(CadastroPostRequestDTO dto) {
        LeadInstagram lead = buscarLeadValido(dto.token());

        String email = dto.email().trim().toLowerCase();
        boolean pessoaJuridica = dto.tipoCliente() == TipoCliente.PESSOA_JURIDICA;
        String cnpj = somenteDigitos(dto.cnpj());

        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Cadastro negado - e-mail já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }
        if (usuarioRepository.existsByInstagramId(lead.getInstagramId())) {
            log.warn("Cadastro negado - conta já cadastrada: {}", lead.getInstagramId());
            throw new ConflictException("Conta já cadastrada");
        }
        if (pessoaJuridica) {
            if (cnpj.isEmpty()) {
                log.warn("Cadastro negado - CNPJ obrigatório não informado: {}", email);
                throw new BusinessValidationException("CNPJ é obrigatório");
            }
            if (vazio(dto.razaoSocial())) {
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
        usuario.setCpf(somenteDigitos(dto.cpf()));
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setInstagramId(lead.getInstagramId());
        usuario.setEtapaFunil(Boolean.TRUE.equals(lead.getSaoPaulo())
                ? EtapaFunil.QUALIFICADO
                : EtapaFunil.EM_QUALIFICACAO);

        if (pessoaJuridica) {
            Empresa empresa = empresaRepository.save(Empresa.builder()
                    .cnpj(cnpj)
                    .razaoSocial(dto.razaoSocial().trim())
                    .nomeFantasia(vazio(dto.nomeFantasia()) ? null : dto.nomeFantasia().trim())
                    .telefone(dto.telefone().trim())
                    .build());
            usuario.setEmpresa(empresa);
            usuario.setTelefone(null);
        }

        usuario = usuarioRepository.save(usuario);

        chatbotLogRepository.vincularUsuario(lead.getInstagramId(), usuario);

        lead.setUsuario(usuario);
        lead.setTokenUsadoEm(LocalDateTime.now());
        leadRepository.save(lead);

        log.info("Cadastro realizado com sucesso - id: {}", usuario.getId());
        return mapper.toResponse(usuario);
    }

    private LeadInstagram buscarLeadValido(String token) {
        LeadInstagram lead = leadRepository.findByTokenHash(tokenService.hash(token))
                .orElseThrow(this::linkInvalido);

        if (lead.getTokenUsadoEm() != null
                || lead.getTokenExpiraEm() == null
                || lead.getTokenExpiraEm().isBefore(LocalDateTime.now())) {
            throw linkInvalido();
        }
        return lead;
    }

    private ResponseStatusException linkInvalido() {
        log.warn("Link de cadastro inválido ou expirado");
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado");
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}