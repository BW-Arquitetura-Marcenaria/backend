package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.response.MinhaContaGetResponseDTO;
import br.com.bw.backend.entity.Empresa;
import br.com.bw.backend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinhaContaMapper {

    MinhaContaGetResponseDTO toResponse(Usuario usuario);

    MinhaContaGetResponseDTO.EmpresaResumo toCompanySummary(Empresa empresa);
}