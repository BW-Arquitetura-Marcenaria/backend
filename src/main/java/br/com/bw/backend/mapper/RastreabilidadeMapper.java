package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.RastreabilidadePostRequestDTO;
import br.com.bw.backend.dto.request.RastreabilidadePutRequestDTO;
import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.entity.Rastreabilidade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RastreabilidadeMapper {
    RastreabilidadeGetResponseDTO toRastreabilidadeGetResponseDTO(Rastreabilidade rastreabilidade);

    default Page<RastreabilidadeGetResponseDTO> toRastreabilidadeGetResponseDTO(Page<Rastreabilidade> rastreabilidade) {
        return rastreabilidade.map(this::toRastreabilidadeGetResponseDTO);
    }

    Rastreabilidade toRastreabilidade(RastreabilidadePostRequestDTO rastreabilidadePostRequestDTO);

    Rastreabilidade toRastreabilidade(RastreabilidadePutRequestDTO rastreabilidadePutRequestDTO);
}
