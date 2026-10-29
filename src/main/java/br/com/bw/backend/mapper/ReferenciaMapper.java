package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.ReferenciaPostRequestDTO;
import br.com.bw.backend.dto.request.ReferenciaPutRequestDTO;
import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.entity.Referencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReferenciaMapper {
    ReferenciaGetResponseDTO toReferenciaGetResponseDTO(Referencia referencia);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Referencia toReferencia(ReferenciaPostRequestDTO referenciaPostRequestDTO);

    @Mapping(target = "id", ignore = true)
    Referencia toReferencia(ReferenciaPutRequestDTO referenciaPutRequestDTO);
}
