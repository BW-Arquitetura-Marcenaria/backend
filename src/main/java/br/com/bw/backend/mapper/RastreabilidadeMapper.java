package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.RastreabilidadePostRequestDTO;
import br.com.bw.backend.dto.request.RastreabilidadePutRequestDTO;
import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.entity.Rastreabilidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RastreabilidadeMapper {
    RastreabilidadeGetResponseDTO toRastreabilidadeGetResponseDTO(Rastreabilidade rastreabilidade);

    default Page<RastreabilidadeGetResponseDTO> toRastreabilidadeGetResponseDTO(Page<Rastreabilidade> rastreabilidade) {
        return rastreabilidade.map(this::toRastreabilidadeGetResponseDTO);
    }

    @Mapping(target = "projeto.id", source = "projetoId")
    Rastreabilidade toRastreabilidade(RastreabilidadePostRequestDTO rastreabilidadePostRequestDTO);

    @Mapping(target = "projeto.id", source = "projetoId")
    Rastreabilidade toRastreabilidade(RastreabilidadePutRequestDTO rastreabilidadePutRequestDTO);

    default LocalDateTime map(LocalDate dataRegistro) {
        return dataRegistro == null ? null : dataRegistro.atStartOfDay();
    }
}
