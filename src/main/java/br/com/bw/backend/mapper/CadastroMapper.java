package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.CadastroPostRequestDTO;
import br.com.bw.backend.dto.response.CadastroGetResponseDTO;
import br.com.bw.backend.dto.response.CadastroValidarGetResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CadastroMapper {

    CadastroGetResponseDTO toResponse(Usuario usuario);

    CadastroValidarGetResponseDTO toValidarResponse(LeadInstagram lead);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etapaFunil", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "instagramId", ignore = true)
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Usuario toEntity(CadastroPostRequestDTO dto);
}