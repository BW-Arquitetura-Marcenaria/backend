package br.com.bw.backend.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnidadeReferencia {
    UNIDADE("Unidade"),
    METRO_QUADRADO("Metro Quadrado"),
    METRO_LINEAR("Metro Linear"),
    HORA("Hora");

    private final String valor;
}
