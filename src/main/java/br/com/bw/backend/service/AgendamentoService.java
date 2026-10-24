package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.CriarEventoGoogleRequest;
import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
import br.com.bw.backend.dto.response.EventoAlteradoGoogleResponse;
import br.com.bw.backend.dto.response.EventoGoogleResponse;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.StatusAgendamento;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.GoogleCalendarDesconectadoException;
import br.com.bw.backend.exception.GoogleCalendarIntegrationException;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.repository.AgendamentoRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgendamentoService {

    private static final List<StatusAgendamento> STATUS_QUE_OCUPAM_AGENDA = List.of(
            StatusAgendamento.PENDENTE,
            StatusAgendamento.APROVADO
    );

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final GoogleCalendarService googleCalendarService;

    @Transactional
    public Agendamento solicitar(
            Integer usuarioId,
            String titulo,
            String descricao,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        validarPeriodo(titulo, inicio, fim);
        validarConflitoDeHorario(inicio, fim);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException(
                        "Usuário não encontrado."
                ));

        Agendamento agendamento = agendamentoMapper.toEntity(
                new SolicitacaoAgendamentoRequest(
                        titulo,
                        descricao,
                        inicio,
                        fim
                )
        );

        agendamento.setUsuario(usuario);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento criarEAprovarComoAdmin(
            Integer adminUsuarioId,
            String titulo,
            String descricao,
            LocalDateTime inicio,
            LocalDateTime fim,
            String observacao
    ) {
        validarPeriodo(titulo, inicio, fim);

        Usuario admin = usuarioRepository.findById(adminUsuarioId)
                .orElseThrow(() -> new NotFoundException(
                        "Usuário não encontrado."
                ));

        Agendamento agendamento = agendamentoMapper.toEntity(
                new SolicitacaoAgendamentoRequest(
                        titulo,
                        descricao,
                        inicio,
                        fim
                )
        );

        agendamento.setUsuario(admin);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        String googleEventId = criarEventoNoGoogleCalendar(agendamento);

        try {
            agendamento.setStatus(StatusAgendamento.APROVADO);
            agendamento.setObservacaoAdmin(observacao);
            agendamento.setGoogleEventId(googleEventId);

            return agendamentoRepository.saveAndFlush(agendamento);
        } catch (RuntimeException e) {
            compensarCriacaoDeEvento(googleEventId);
            throw e;
        }
    }

    private void validarPeriodo(String titulo, LocalDateTime inicio, LocalDateTime fim) {
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessValidationException(
                    "O título do agendamento é obrigatório."
            );
        }

        if (inicio == null || fim == null) {
            throw new BusinessValidationException(
                    "A data de início e a data de fim são obrigatórias."
            );
        }

        if (!fim.isAfter(inicio)) {
            throw new BusinessValidationException(
                    "O horário de fim deve ser posterior ao início."
            );
        }
    }

    private void validarConflitoDeHorario(LocalDateTime inicio, LocalDateTime fim) {
        boolean existeConflito = agendamentoRepository.existeConflitoDeHorario(
                inicio, fim, STATUS_QUE_OCUPAM_AGENDA
        );

        if (existeConflito) {
            throw new BusinessValidationException(
                    "Já existe um agendamento pendente ou aprovado nesse horário."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAllByOrderByCriadoEmDesc();
    }

    @Transactional
    public Agendamento aprovar(Integer agendamentoId, String observacao) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado."));

        int linhasAtualizadas = agendamentoRepository.atualizarStatusSeStatusAtualFor(
                agendamentoId,
                StatusAgendamento.PENDENTE,
                StatusAgendamento.APROVADO,
                observacao
        );

        if (linhasAtualizadas == 0) {
            throw new BusinessValidationException(
                    "Somente agendamentos pendentes podem receber uma decisão.");
        }

        String googleEventId = criarEventoNoGoogleCalendar(agendamento);

        try {
            agendamento.setStatus(StatusAgendamento.APROVADO);
            agendamento.setObservacaoAdmin(observacao);
            agendamento.setGoogleEventId(googleEventId);

            return agendamentoRepository.saveAndFlush(agendamento);
        } catch (RuntimeException e) {
            compensarCriacaoDeEvento(googleEventId);
            throw e;
        }
    }

    private void compensarCriacaoDeEvento(String googleEventId) {
        try {
            googleCalendarService.excluirEvento(googleEventId);
        } catch (Exception compensacaoFalhou) {
            log.error(
                    "Falha ao compensar (excluir) o evento '{}' no Google Calendar após erro de "
                            + "persistência local. O evento pode ter ficado órfão na agenda e precisa "
                            + "ser removido manualmente.",
                    googleEventId,
                    compensacaoFalhou
            );
        }
    }

    private String criarEventoNoGoogleCalendar(Agendamento agendamento) {
        CriarEventoGoogleRequest request = new CriarEventoGoogleRequest(
                agendamento.getTitulo(),
                agendamento.getDescricao(),
                agendamento.getInicio(),
                agendamento.getFim()
        );

        try {
            EventoGoogleResponse resposta = googleCalendarService.criarEvento(request);
            return resposta.id();
        } catch (GoogleCalendarDesconectadoException e) {
            throw e;
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleCalendarIntegrationException(
                    "Não foi possível criar o evento no Google Calendar.", e
            );
        }
    }


    @Transactional
    public Agendamento recusar(Integer agendamentoId, String observacao) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado."));

        int linhasAtualizadas = agendamentoRepository.atualizarStatusSeStatusAtualFor(
                agendamentoId,
                StatusAgendamento.PENDENTE,
                StatusAgendamento.RECUSADO,
                observacao
        );

        if (linhasAtualizadas == 0) {
            throw new BusinessValidationException(
                    "Somente agendamentos pendentes podem receber uma decisão.");
        }

        agendamento.setStatus(StatusAgendamento.RECUSADO);
        agendamento.setObservacaoAdmin(observacao);

        return agendamento;
    }

    @Transactional
    public void reconciliarComGoogle(EventoAlteradoGoogleResponse evento) {
        agendamentoRepository.findByGoogleEventId(evento.id()).ifPresent(agendamento -> {
            if (agendamento.getStatus() != StatusAgendamento.APROVADO) {
                return;
            }

            if ("cancelled".equalsIgnoreCase(evento.status())) {
                agendamento.setStatus(StatusAgendamento.CANCELADO);
                agendamento.setObservacaoAdmin(anexarObservacao(
                        agendamento.getObservacaoAdmin(),
                        "Evento removido diretamente no Google Calendar."
                ));
                agendamentoRepository.save(agendamento);
                log.info(
                        "Agendamento {} marcado como CANCELADO: evento removido no Google Calendar.",
                        agendamento.getId()
                );
                return;
            }

            boolean horarioAlterado = evento.inicio() != null && evento.fim() != null
                    && (!evento.inicio().equals(agendamento.getInicio())
                        || !evento.fim().equals(agendamento.getFim()));

            if (horarioAlterado) {
                agendamento.setInicio(evento.inicio());
                agendamento.setFim(evento.fim());
                agendamentoRepository.save(agendamento);
                log.info(
                        "Agendamento {} teve o horário atualizado a partir do Google Calendar.",
                        agendamento.getId()
                );
            }
        });
    }

    private String anexarObservacao(String existente, String nova) {
        if (existente == null || existente.isBlank()) {
            return nova;
        }

        return existente + " | " + nova;
    }
}

