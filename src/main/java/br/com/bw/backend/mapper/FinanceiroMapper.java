package br.com.bw.backend.mapper;

import br.com.bw.backend.dto.request.FinanceiroPostRequestDTO;
import br.com.bw.backend.dto.request.FinanceiroPutRequestDTO;
import br.com.bw.backend.dto.response.FinanceiroGetResponseDTO;
import br.com.bw.backend.entity.Financeiro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FinanceiroMapper {
    FinanceiroGetResponseDTO toFinanceiroGetResponseDTO(Financeiro financeiro);

    default Page<FinanceiroGetResponseDTO> toFinanceiroGetResponseDTO(Page<Financeiro> financeiro) {
        return financeiro.map(this::toFinanceiroGetResponseDTO);
    }

    @Mapping(target = "id", ignore = true)
    Financeiro toFinanceiro(FinanceiroPostRequestDTO financeiroPostRequestDTO);

    @Mapping(target = "id", ignore = true)
    Financeiro toFinanceiro(FinanceiroPutRequestDTO financeiroPutRequestDTO);
}
