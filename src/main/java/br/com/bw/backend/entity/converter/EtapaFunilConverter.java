package br.com.bw.backend.entity.converter;

import br.com.bw.backend.entity.enums.EtapaFunil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EtapaFunilConverter implements AttributeConverter<EtapaFunil, String> {

    @Override
    public String convertToDatabaseColumn(EtapaFunil etapa) {
        return etapa == null ? null : etapa.getValor();
    }

    @Override
    public EtapaFunil convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        for (EtapaFunil etapa : EtapaFunil.values()) {
            if (etapa.getValor().equals(valor)) {
                return etapa;
            }
        }
        throw new IllegalArgumentException("Etapa do funil inválida: " + valor);
    }
}