package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.entity.Referencia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReferenciaMapper {
    ReferenciaGetResponseDTO toReferenciaGetResponseDTO(Referencia referencia);
}
