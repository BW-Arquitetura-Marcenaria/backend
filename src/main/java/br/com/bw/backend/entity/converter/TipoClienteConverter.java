package br.com.bw.backend.entity.converter;

import br.com.bw.backend.entity.enums.TipoCliente;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoClienteConverter implements AttributeConverter<TipoCliente, String> {

    @Override
    public String convertToDatabaseColumn(TipoCliente tipo) {
        return tipo == null ? null : tipo.getValor();
    }

    @Override
    public TipoCliente convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        for (TipoCliente tipo : TipoCliente.values()) {
            if (tipo.getValor().equals(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de cliente inválido: " + valor);
    }
}