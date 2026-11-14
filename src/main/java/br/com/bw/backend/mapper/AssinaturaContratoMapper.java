package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.response.AssinaturaContratoGetResponseDTO;
import br.com.bw.backend.dto.response.AssinaturaSignatarioGetResponseDTO;
import br.com.bw.backend.entity.AssinaturaContrato;
import br.com.bw.backend.entity.AssinaturaSignatario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssinaturaContratoMapper {

    @Mapping(target = "projetoId", source = "projeto.id")
    AssinaturaContratoGetResponseDTO toAssinaturaContratoGetResponseDTO(AssinaturaContrato assinatura);

    List<AssinaturaContratoGetResponseDTO> toAssinaturaContratoGetResponseDTO(List<AssinaturaContrato> assinaturas);

    AssinaturaSignatarioGetResponseDTO toAssinaturaSignatarioGetResponseDTO(AssinaturaSignatario signatario);
}
