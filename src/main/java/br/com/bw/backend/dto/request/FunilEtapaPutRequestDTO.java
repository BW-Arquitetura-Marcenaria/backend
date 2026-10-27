package br.com.bw.backend.dto.request;

import br.com.bw.backend.entity.enums.EtapaFunil;
import jakarta.validation.constraints.NotNull;

public record FunilEtapaPutRequestDTO(
        @NotNull
        EtapaFunil etapa
) {
}