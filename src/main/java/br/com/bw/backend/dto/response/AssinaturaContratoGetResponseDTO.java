package br.com.bw.backend.dto.response;

import br.com.bw.backend.entity.enums.StatusAssinatura;

import java.time.LocalDateTime;
import java.util.List;

public record AssinaturaContratoGetResponseDTO(
        Integer id,
        Integer projetoId,
        String envelopeId,
        String documentId,
        String nomeArquivo,
        StatusAssinatura status,
        LocalDateTime dataEnvio,
        LocalDateTime dataAtualizacao,
        List<AssinaturaSignatarioGetResponseDTO> signatarios
) {
}
