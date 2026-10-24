package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
import br.com.bw.backend.entity.Agendamento;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.StatusAgendamento;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.mapper.AgendamentoMapper;
import br.com.bw.backend.repository.AgendamentoRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendamentoMapper agendamentoMapper;

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
}

