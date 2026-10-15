package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.ProjetoPostRequestDTO;
import br.com.bw.backend.dto.request.ProjetoPutRequestDTO;
import br.com.bw.backend.dto.response.ProjetoGetResponseDTO;
import br.com.bw.backend.entity.Projeto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjetoMapper {
    @Mapping(target = "clienteId", source = "cliente.id")
    ProjetoGetResponseDTO toProjetoGetResponseDTO(Projeto projeto);

    default Page<ProjetoGetResponseDTO> toProjetoGetResponseDTO(Page<Projeto> projeto) {
        return projeto.map(this::toProjetoGetResponseDTO);
    }

    @Mapping(target = "cliente", ignore = true)
    Projeto toProjeto(ProjetoPostRequestDTO projetoPostRequestDTO);

    @Mapping(target = "cliente", ignore = true)
    Projeto toProjeto(ProjetoPutRequestDTO projetoPutRequestDTO);
}