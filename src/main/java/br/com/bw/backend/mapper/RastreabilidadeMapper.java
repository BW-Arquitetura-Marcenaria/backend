package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.entity.Rastreabilidade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RastreabilidadeMapper {
    RastreabilidadeGetResponseDTO toRastreabilidadeGetResponseDTO(Rastreabilidade rastreabilidade);
}
