package br.com.bw.backend.entity.converter;

import br.com.bw.backend.entity.enums.CategoriaReferencia;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoriaReferenciaConverter implements AttributeConverter<CategoriaReferencia, String> {

    @Override
    public String convertToDatabaseColumn(CategoriaReferencia categoria) {
        return categoria == null ? null : categoria.getValor();
    }

    @Override
    public CategoriaReferencia convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        for (CategoriaReferencia categoria : CategoriaReferencia.values()) {
            if (categoria.getValor().equals(valor)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria de referência inválida: " + valor);
    }
}
