package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.AssistenteMensagemPostRequestDTO;
import br.com.bw.backend.dto.response.AssistenteHistoricoGetResponseDTO;
import br.com.bw.backend.dto.response.AssistenteHistoricoGetResponseDTO.Mensagem;
import br.com.bw.backend.dto.response.AssistenteMensagemPostResponseDTO;
import br.com.bw.backend.entity.ChatbotLog;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.repository.ChatbotLogRepository;
import br.com.bw.backend.repository.LeadInstagramRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistenteService {

    private static final String AUTOR_CLIENTE = "CLIENTE";
    private static final String AUTOR_ASSISTENTE = "ASSISTENTE";

    private final UsuarioRepository usuarioRepository;
    private final LeadInstagramRepository leadRepository;
    private final ChatbotLogRepository chatbotLogRepository;
    private final N8nAssistenteService n8nService;

    public AssistenteMensagemPostResponseDTO sendMessage(Integer usuarioId, AssistenteMensagemPostRequestDTO dto) {
        Usuario cliente = findClient(usuarioId);
        String mensagem = dto.mensagem().trim();

        N8nAssistenteService.Resposta resposta =
                n8nService.send(cliente.getId(), cliente.getNome(), cliente.getEmail(), mensagem);

        chatbotLogRepository.save(ChatbotLog.builder()
                .cliente(cliente)
                .mensagemUsuario(mensagem)
                .respostaIa(resposta.resposta())
                .build());

        moveToQualification(usuarioId);

        boolean concluido = Boolean.TRUE.equals(resposta.concluido());
        log.info("Mensagem ao assistente - cliente: {}, concluido: {}", usuarioId, concluido);
        return new AssistenteMensagemPostResponseDTO(resposta.resposta(), concluido);
    }

    @Transactional(readOnly = true)
    public AssistenteHistoricoGetResponseDTO findHistory(Integer usuarioId) {
        Usuario cliente = findClient(usuarioId);

        List<Mensagem> mensagens = new ArrayList<>();
        for (ChatbotLog registro : chatbotLogRepository
                .findByClienteIdOrderByDataInteracaoAscIdAsc(cliente.getId())) {
            mensagens.add(new Mensagem(AUTOR_CLIENTE, registro.getMensagemUsuario(), registro.getDataInteracao()));
            mensagens.add(new Mensagem(AUTOR_ASSISTENTE, registro.getRespostaIa(), registro.getDataInteracao()));
        }

        boolean concluido = leadRepository.findByUsuarioId(cliente.getId())
                .map(lead -> lead.getResumoConversa() != null && !lead.getResumoConversa().isBlank())
                .orElse(false);

        return new AssistenteHistoricoGetResponseDTO(concluido, mensagens);
    }

    private void moveToQualification(Integer usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(atual -> {
            if (atual.getEtapaFunil() == null || atual.getEtapaFunil() == EtapaFunil.NOVO_LEAD) {
                atual.setEtapaFunil(EtapaFunil.EM_QUALIFICACAO);
                usuarioRepository.save(atual);
            }
        });
    }

    private Usuario findClient(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado"));
    }
}