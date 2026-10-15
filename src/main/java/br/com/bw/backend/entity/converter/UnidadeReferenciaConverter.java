package br.com.bw.backend.entity.converter;

import br.com.bw.backend.entity.enums.UnidadeReferencia;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UnidadeReferenciaConverter implements AttributeConverter<UnidadeReferencia, String> {

    @Override
    public String convertToDatabaseColumn(UnidadeReferencia unidade) {
        return unidade == null ? null : unidade.getValor();
    }

    @Override
    public UnidadeReferencia convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        for (UnidadeReferencia unidade : UnidadeReferencia.values()) {
            if (unidade.getValor().equals(valor)) {
                return unidade;
            }
        }
        throw new IllegalArgumentException("Unidade de referência inválida: " + valor);
    }
}
