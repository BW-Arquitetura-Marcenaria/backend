package br.com.bw.backend.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EtapaFunil {
    NOVO_LEAD("Novo Lead"),
    EM_QUALIFICACAO("Em Qualificacao"),
    QUALIFICADO("Qualificado"),
    APRESENTACAO("Apresentacao"),
    EM_NEGOCIACAO("Em Negociacao"),
    FECHADO("Fechado");

    private final String valor;
}