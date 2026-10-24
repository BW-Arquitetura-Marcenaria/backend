package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.ConviteAdminAceitarPostRequestDTO;
import br.com.bw.backend.dto.request.ConviteAdminPostRequestDTO;
import br.com.bw.backend.dto.request.ConviteAdminRecusarPostRequestDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO.AdministradorItem;
import br.com.bw.backend.dto.response.ConviteAdminValidacaoGetResponseDTO;
import br.com.bw.backend.entity.ConviteAdmin;
import br.com.bw.backend.entity.Empresa;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.entity.enums.StatusConvite;
import br.com.bw.backend.entity.enums.TipoCliente;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.ConflictException;
import br.com.bw.backend.repository.ConviteAdminRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConviteAdminService {

    private static final String TIPO_USUARIO = "USUARIO";
    private static final String TIPO_CONVITE = "CONVITE";

    private final ConviteAdminRepository conviteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.convite-admin.expiracao-horas}")
    private int expiracaoHoras;

    @Transactional(readOnly = true)
    public AdministradoresGetResponseDTO listar(Integer usuarioId) {
        Usuario admin = adminLogado(usuarioId);
        Empresa empresa = empresaDe(admin);

        List<Usuario> admins = usuarioRepository
                .findByEmpresaIdAndPerfilOrderByIdAsc(empresa.getId(), Perfil.ADMIN);

        List<AdministradorItem> itens = new ArrayList<>();
        long ativos = 0;

        for (Usuario u : admins) {
            boolean ativo = Boolean.TRUE.equals(u.getAtivo());
            if (ativo) {
                ativos++;
            }
            itens.add(new AdministradorItem(
                    u.getId(),
                    TIPO_USUARIO,
                    nomeCompleto(u),
                    u.getEmail(),
                    u.getCpf(),
                    ativo ? "ATIVO" : "DESATIVADO"));
        }

        LocalDateTime agora = LocalDateTime.now();
        conviteRepository
                .findByEmpresaIdAndStatusOrderByCriadoEmDesc(empresa.getId(), StatusConvite.PENDENTE)
                .stream()
                .filter(c -> c.getExpiraEm().isAfter(agora))
                .forEach(c -> itens.add(itemDoConvite(c)));

        return new AdministradoresGetResponseDTO(ativos, itens);
    }

    @Transactional
    public AdministradorItem convidar(Integer usuarioId, ConviteAdminPostRequestDTO dto) {
        Usuario admin = adminLogado(usuarioId);
        Empresa empresa = empresaDe(admin);

        encerrarConvitesVencidos();

        String nomeCompleto = dto.nomeCompleto().trim().replaceAll("\\s+", " ");
        String email = dto.email().trim().toLowerCase();
        String cpf = somenteDigitos(dto.cpf());

        if (!nomeCompleto.contains(" ")) {
            throw new BusinessValidationException("Informe nome e sobrenome");
        }
        if (cpf.length() != 11) {
            throw new BusinessValidationException("CPF inválido");
        }
        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Convite negado - e-mail já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            log.warn("Convite negado - CPF já cadastrado");
            throw new ConflictException("CPF já cadastrado");
        }
        if (conviteRepository.existsByEmailAndStatus(email, StatusConvite.PENDENTE)) {
            throw new ConflictException("Já existe um convite pendente para este e-mail");
        }
        if (conviteRepository.existsByCpfAndStatus(cpf, StatusConvite.PENDENTE)) {
            throw new ConflictException("Já existe um convite pendente para este CPF");
        }

        String token = tokenService.gerar();

        ConviteAdmin convite = conviteRepository.save(ConviteAdmin.builder()
                .empresa(empresa)
                .nomeCompleto(nomeCompleto)
                .email(email)
                .cpf(cpf)
                .tokenHash(tokenService.hash(token))
                .expiraEm(LocalDateTime.now().plusHours(expiracaoHoras))
                .convidadoPor(admin.getId())
                .build());

        // Se o envio falhar, a exceção desfaz a transação e o convite não fica gravado
        emailService.enviarConviteAdmin(email, nomeCompleto, nomeDaEmpresa(empresa), token);

        log.info("Convite de administrador enviado - id: {}", convite.getId());
        return itemDoConvite(convite);
    }

    @Transactional(readOnly = true)
    public ConviteAdminValidacaoGetResponseDTO validar(String token) {
        ConviteAdmin convite = buscarConviteValido(token);
        return new ConviteAdminValidacaoGetResponseDTO(
                convite.getNomeCompleto(),
                convite.getEmail(),
                nomeDaEmpresa(convite.getEmpresa()),
                convite.getExpiraEm());
    }

    @Transactional
    public void aceitar(ConviteAdminAceitarPostRequestDTO dto) {
        if (!dto.senha().equals(dto.confirmarSenha())) {
            throw new BusinessValidationException("As senhas não conferem");
        }

        ConviteAdmin convite = buscarConviteValido(dto.token());

        if (usuarioRepository.existsByEmail(convite.getEmail())) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (usuarioRepository.existsByCpf(convite.getCpf())) {
            throw new ConflictException("CPF já cadastrado");
        }

        int espaco = convite.getNomeCompleto().indexOf(' ');
        String nome = convite.getNomeCompleto().substring(0, espaco);
        String sobrenome = convite.getNomeCompleto().substring(espaco + 1);

        Usuario novo = usuarioRepository.save(Usuario.builder()
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .nome(nome)
                .sobrenome(sobrenome)
                .cpf(convite.getCpf())
                .email(convite.getEmail())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .ativo(true)
                .perfil(Perfil.ADMIN)
                .empresa(convite.getEmpresa())
                .build());

        convite.setStatus(StatusConvite.ACEITO);
        convite.setRespondidoEm(LocalDateTime.now());
        convite.setUsuarioId(novo.getId());

        log.info("Convite aceito - novo administrador id: {}", novo.getId());
    }

    @Transactional
    public void recusar(ConviteAdminRecusarPostRequestDTO dto) {
        ConviteAdmin convite = buscarConviteValido(dto.token());

        convite.setStatus(StatusConvite.RECUSADO);
        convite.setRespondidoEm(LocalDateTime.now());

        log.info("Convite recusado - id: {}", convite.getId());
    }

    @Transactional
    public void desativar(Integer usuarioId, Integer alvoId) {
        Usuario admin = adminLogado(usuarioId);
        Empresa empresa = empresaDe(admin);

        Usuario alvo = usuarioRepository.findById(alvoId)
                .filter(u -> u.getPerfil() == Perfil.ADMIN)
                .filter(u -> u.getEmpresa() != null && u.getEmpresa().getId().equals(empresa.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador não encontrado"));

        if (alvo.getId().equals(admin.getId())) {
            throw new BusinessValidationException("Você não pode desativar o seu próprio acesso");
        }

        long ativos = usuarioRepository
                .findByEmpresaIdAndPerfilOrderByIdAsc(empresa.getId(), Perfil.ADMIN)
                .stream()
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .count();

        if (Boolean.TRUE.equals(alvo.getAtivo()) && ativos <= 1) {
            throw new BusinessValidationException("Não é possível desativar o último administrador ativo");
        }

        alvo.setAtivo(false);
        log.info("Administrador desativado - id: {} por: {}", alvo.getId(), admin.getId());
    }

    private ConviteAdmin buscarConviteValido(String token) {
        ConviteAdmin convite = conviteRepository.findByTokenHash(tokenService.hash(token))
                .orElseThrow(this::conviteInvalido);

        if (convite.getStatus() != StatusConvite.PENDENTE
                || convite.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw conviteInvalido();
        }
        return convite;
    }

    private ResponseStatusException conviteInvalido() {
        log.warn("Convite de administrador inválido, respondido ou expirado");
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Convite inválido ou expirado");
    }

    private void encerrarConvitesVencidos() {
        conviteRepository.findByStatusAndExpiraEmBefore(StatusConvite.PENDENTE, LocalDateTime.now())
                .forEach(c -> c.setStatus(StatusConvite.REVOGADO));
    }

    private Usuario adminLogado(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado"));
        if (usuario.getPerfil() != Perfil.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return usuario;
    }

    private Empresa empresaDe(Usuario admin) {
        if (admin.getEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma empresa vinculada a este administrador");
        }
        return admin.getEmpresa();
    }

    private AdministradorItem itemDoConvite(ConviteAdmin c) {
        return new AdministradorItem(
                c.getId(),
                TIPO_CONVITE,
                c.getNomeCompleto(),
                c.getEmail(),
                c.getCpf(),
                "CONVITE_ENVIADO");
    }

    private String nomeCompleto(Usuario u) {
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome();
        return (u.getNome() + " " + sobrenome).trim();
    }

    private String nomeDaEmpresa(Empresa empresa) {
        return vazio(empresa.getNomeFantasia()) ? empresa.getRazaoSocial() : empresa.getNomeFantasia();
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}