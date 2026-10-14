package br.com.bw.backend.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusProjeto {
    PLANEJAMENTO("Planejamento"),
    EM_EXECUCAO("Em Execucao"),
    CONCLUIDO("Concluido"),
    CANCELADO("Cancelado");

    private final String valor;
}
