package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.MensagemChatPostRequestDTO;
import br.com.bw.backend.dto.response.ConversaChatGetResponseDTO;
import br.com.bw.backend.dto.response.MensagemChatGetResponseDTO;
import br.com.bw.backend.dto.response.NaoLidasGetResponseDTO;
import br.com.bw.backend.dto.response.ResumoAtendimentoGetResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.MensagemChat;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.entity.enums.RemetenteChat;
import br.com.bw.backend.event.MensagemChatCriadaEvent;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.MensagemChatRepository;
import br.com.bw.backend.repository.MensagemChatRepository.ContagemNaoLidas;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private static final int TAMANHO_PREVIA = 120;

    private final MensagemChatRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeadInstagramRepository leadRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatRateLimitService rateLimitService;

    // ---------- Cliente ----------

    @Transactional(readOnly = true)
    public List<MensagemChatGetResponseDTO> historicoDoCliente(Integer usuarioId, Integer depois) {
        Usuario cliente = clienteLogado(usuarioId);
        return buscarMensagens(cliente.getId(), depois);
    }

    @Transactional
    public MensagemChatGetResponseDTO enviarComoCliente(Integer usuarioId, MensagemChatPostRequestDTO dto) {
        Usuario cliente = clienteLogado(usuarioId);
        return salvar(cliente, cliente.getId(), RemetenteChat.CLIENTE, dto);
    }

    @Transactional(readOnly = true)
    public NaoLidasGetResponseDTO naoLidasDoCliente(Integer usuarioId) {
        Usuario cliente = clienteLogado(usuarioId);
        return new NaoLidasGetResponseDTO(mensagemRepository
                .countByClienteIdAndRemetenteAndLidaEmIsNull(cliente.getId(), RemetenteChat.ADMIN));
    }

    @Transactional
    public void marcarComoLidasPeloCliente(Integer usuarioId) {
        Usuario cliente = clienteLogado(usuarioId);
        mensagemRepository.marcarComoLidas(cliente.getId(), RemetenteChat.ADMIN, LocalDateTime.now());
    }

    // ---------- Admin ----------

    @Transactional(readOnly = true)
    public List<ConversaChatGetResponseDTO> listarConversas(Integer adminId) {
        adminLogado(adminId);

        Map<Integer, Long> naoLidas = new HashMap<>();
        for (ContagemNaoLidas c : mensagemRepository.contarNaoLidasPorConversa(RemetenteChat.CLIENTE)) {
            naoLidas.put(c.getClienteId(), c.getTotal());
        }

        return mensagemRepository.findUltimaMensagemDeCadaConversa().stream()
                .map(m -> new ConversaChatGetResponseDTO(
                        m.getCliente().getId(),
                        nomeCompleto(m.getCliente()),
                        previa(m.getConteudo()),
                        m.getRemetente(),
                        m.getCriadoEm(),
                        naoLidas.getOrDefault(m.getCliente().getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MensagemChatGetResponseDTO> historicoDoAdmin(Integer adminId, Integer clienteId, Integer depois) {
        adminLogado(adminId);
        Usuario cliente = clienteDaConversa(clienteId);
        return buscarMensagens(cliente.getId(), depois);
    }

    @Transactional
    public MensagemChatGetResponseDTO responder(Integer adminId, Integer clienteId, MensagemChatPostRequestDTO dto) {
        Usuario admin = adminLogado(adminId);
        Usuario cliente = clienteDaConversa(clienteId);
        return salvar(cliente, admin.getId(), RemetenteChat.ADMIN, dto);
    }

    @Transactional(readOnly = true)
    public NaoLidasGetResponseDTO naoLidasDoAdmin(Integer adminId) {
        adminLogado(adminId);
        return new NaoLidasGetResponseDTO(mensagemRepository.countByRemetenteAndLidaEmIsNull(RemetenteChat.CLIENTE));
    }

    @Transactional
    public void marcarComoLidasPeloAdmin(Integer adminId, Integer clienteId) {
        adminLogado(adminId);
        Usuario cliente = clienteDaConversa(clienteId);
        mensagemRepository.marcarComoLidas(cliente.getId(), RemetenteChat.CLIENTE, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public ResumoAtendimentoGetResponseDTO resumoDoAtendimento(Integer adminId, Integer clienteId) {
        adminLogado(adminId);
        Usuario cliente = clienteDaConversa(clienteId);

        LeadInstagram lead = leadRepository.findByUsuarioId(cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Este cliente não tem resumo do atendimento"));

        return new ResumoAtendimentoGetResponseDTO(
                lead.getResumoConversa(),
                lead.getAmbiente(),
                lead.getMovel(),
                lead.getCor(),
                lead.getMaterial(),
                lead.getOrcamento(),
                lead.getSituacaoProjeto(),
                lead.getPrecisaVisitaTecnica(),
                lead.getSaoPaulo(),
                lead.getCriadoEm());
    }

    // ---------- Internos ----------

    private List<MensagemChatGetResponseDTO> buscarMensagens(Integer clienteId, Integer depois) {
        int ultimoId = depois == null ? 0 : depois;
        return mensagemRepository.findByClienteIdAndIdGreaterThanOrderByIdAsc(clienteId, ultimoId).stream()
                .map(this::paraDto)
                .toList();
    }

    private MensagemChatGetResponseDTO salvar(Usuario cliente, Integer autorId,
                                              RemetenteChat remetente, MensagemChatPostRequestDTO dto) {
        rateLimitService.validar(autorId);

        // Primeira mensagem nao lida da sequencia deste lado: so ela dispara o aviso por e-mail
        boolean primeiraNaoLida = !mensagemRepository
                .existsByClienteIdAndRemetenteAndLidaEmIsNull(cliente.getId(), remetente);

        MensagemChat mensagem = mensagemRepository.save(MensagemChat.builder()
                .cliente(cliente)
                .autorId(autorId)
                .remetente(remetente)
                .conteudo(dto.conteudo().trim())
                .build());

        MensagemChatGetResponseDTO resposta = paraDto(mensagem);

        // E-mail e WebSocket reagem a este evento depois do commit
        eventPublisher.publishEvent(
                new MensagemChatCriadaEvent(cliente.getId(), remetente, primeiraNaoLida, resposta));

        log.info("Mensagem de chat registrada - id: {} remetente: {} cliente: {}",
                mensagem.getId(), remetente, cliente.getId());
        return resposta;
    }

    private Usuario clienteLogado(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado"));
        if (usuario.getPerfil() != Perfil.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return usuario;
    }

    private Usuario adminLogado(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado"));
        if (usuario.getPerfil() != Perfil.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return usuario;
    }

    private Usuario clienteDaConversa(Integer clienteId) {
        return usuarioRepository.findById(clienteId)
                .filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private MensagemChatGetResponseDTO paraDto(MensagemChat m) {
        return new MensagemChatGetResponseDTO(m.getId(), m.getRemetente(), m.getConteudo(), m.getCriadoEm());
    }

    private String nomeCompleto(Usuario u) {
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome();
        return (u.getNome() + " " + sobrenome).trim();
    }

    private String previa(String conteudo) {
        if (conteudo.length() <= TAMANHO_PREVIA) {
            return conteudo;
        }
        return conteudo.substring(0, TAMANHO_PREVIA - 3) + "...";
    }
}