package br.com.bw.backend.entity.converter;

import br.com.bw.backend.entity.enums.StatusProjeto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusProjetoConverter implements AttributeConverter<StatusProjeto, String> {

    @Override
    public String convertToDatabaseColumn(StatusProjeto status) {
        return status == null ? null : status.getValor();
    }

    @Override
    public StatusProjeto convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        for (StatusProjeto status : StatusProjeto.values()) {
            if (status.getValor().equals(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de projeto inválido: " + valor);
    }
}
