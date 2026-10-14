package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.CadastroPostRequestDTO;
import br.com.bw.backend.dto.response.CadastroGetResponseDTO;
import br.com.bw.backend.dto.response.CadastroValidarGetResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.TipoCliente;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.ConflictException;
import br.com.bw.backend.mapper.CadastroMapper;
import br.com.bw.backend.repository.ChatbotLogRepository;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CadastroService {

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
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

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (usuarioRepository.existsByInstagramId(lead.getInstagramId())) {
            throw new ConflictException("Conta já cadastrada");
        }
        if (dto.tipoCliente() == TipoCliente.PESSOA_FISICA && vazio(dto.cpf())) {
            throw new BusinessValidationException("CPF é obrigatório");
        }
        if (dto.tipoCliente() == TipoCliente.PESSOA_JURIDICA && vazio(dto.cnpj())) {
            throw new BusinessValidationException("CNPJ é obrigatório");
        }

        Usuario usuario = mapper.toEntity(dto);
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setInstagramId(lead.getInstagramId());
        usuario.setEtapaFunil(Boolean.TRUE.equals(lead.getSaoPaulo())
                ? EtapaFunil.QUALIFICADO
                : EtapaFunil.EM_QUALIFICACAO);

        usuario = usuarioRepository.save(usuario);

        chatbotLogRepository.vincularUsuario(lead.getInstagramId(), usuario);

        lead.setUsuario(usuario);
        lead.setTokenUsadoEm(LocalDateTime.now());
        leadRepository.save(lead);

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
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado");
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}