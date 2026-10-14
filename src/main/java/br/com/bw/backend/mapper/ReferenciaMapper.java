package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.ReferenciaPostRequestDTO;
import br.com.bw.backend.dto.request.ReferenciaPutRequestDTO;
import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.entity.Referencia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReferenciaMapper {
    ReferenciaGetResponseDTO toReferenciaGetResponseDTO(Referencia referencia);

    Referencia toReferencia(ReferenciaPostRequestDTO referenciaPostRequestDTO);

    Referencia toReferencia(ReferenciaPutRequestDTO referenciaPutRequestDTO);
}
