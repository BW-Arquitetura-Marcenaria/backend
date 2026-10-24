package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.CriarEventoGoogleRequest;
import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

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

    @Transactional(readOnly = true)
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAllByOrderByCriadoEmDesc();
    }

    @Transactional
    public Agendamento aprovar(Integer agendamentoId, String observacao) {
        Agendamento agendamento = buscarPendente(agendamentoId);

        String googleEventId = criarEventoNoGoogleCalendar(agendamento);

        agendamento.setStatus(StatusAgendamento.APROVADO);
        agendamento.setObservacaoAdmin(observacao);
        agendamento.setGoogleEventId(googleEventId);

        return agendamentoRepository.save(agendamento);
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
        Agendamento agendamento = buscarPendente(agendamentoId);

        agendamento.setStatus(StatusAgendamento.RECUSADO);
        agendamento.setObservacaoAdmin(observacao);

        return agendamentoRepository.save(agendamento);
    }

    private Agendamento buscarPendente(Integer agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() ->
                        new NotFoundException("Agendamento não encontrado."));

        if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            throw new BusinessValidationException(
                    "Somente agendamentos pendentes podem receber uma decisão.");
        }

        return agendamento;
    }
}

