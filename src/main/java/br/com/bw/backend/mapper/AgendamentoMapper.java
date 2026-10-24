package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.response.AgendamentoResponse;
import br.com.bw.backend.dto.request.SolicitacaoAgendamentoRequest;
import br.com.bw.backend.entity.Agendamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AgendamentoMapper {

    AgendamentoResponse toResponse(Agendamento agendamento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "googleEventId", ignore = true)
    @Mapping(target = "observacaoAdmin", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Agendamento toEntity(SolicitacaoAgendamentoRequest request);
}
