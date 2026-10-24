package br.com.bw.backend.event;

import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.entity.enums.RemetenteChat;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificacaoListener {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoCriarMensagem(MensagemChatCriadaEvent evento) {
        if (!evento.primeiraNaoLida()) {
            return;
        }

        usuarioRepository.findById(evento.clienteId()).ifPresent(cliente -> {
            if (evento.remetente() == RemetenteChat.CLIENTE) {
                avisarEquipe(cliente);
            } else {
                avisarCliente(cliente);
            }
        });
    }

    private void avisarEquipe(Usuario cliente) {
        String nome = nomeCompleto(cliente);

        for (Usuario admin : usuarioRepository.findByPerfilAndAtivoTrue(Perfil.ADMIN)) {
            try {
                emailService.enviarAvisoNovaMensagemParaEquipe(admin.getEmail(), nome);
            } catch (Exception e) {
                log.warn("Aviso de mensagem não enviado ao administrador id: {}", admin.getId());
            }
        }
    }

    private void avisarCliente(Usuario cliente) {
        try {
            emailService.enviarAvisoRespostaParaCliente(cliente.getEmail(), cliente.getNome());
        } catch (Exception e) {
            log.warn("Aviso de resposta não enviado ao cliente id: {}", cliente.getId());
        }
    }

    private String nomeCompleto(Usuario u) {
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome();
        return (u.getNome() + " " + sobrenome).trim();
    }
}