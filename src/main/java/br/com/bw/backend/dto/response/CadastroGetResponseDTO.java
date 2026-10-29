package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.TipoCliente;

import java.time.LocalDateTime;

public record CadastroGetResponseDTO(
        Integer id,
        TipoCliente tipoCliente,
        String nome,
        String sobrenome,
        String email,
        String telefone,
        EtapaFunil etapaFunil,
        LocalDateTime dataCadastro
) {}
