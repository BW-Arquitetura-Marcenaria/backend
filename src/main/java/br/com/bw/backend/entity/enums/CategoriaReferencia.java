package br.com.bw.backend.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaReferencia {
    MARCENARIA_PLANEJADA("Marcenaria Planejada"),
    DORMITORIO_E_CLOSSET("Dormitorio e Closset"),
    MAO_DE_OBRA("Mao de Obra");

    private final String valor;
}
