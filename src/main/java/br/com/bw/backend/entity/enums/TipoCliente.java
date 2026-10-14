package br.com.bw.backend.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoCliente {
    PESSOA_FISICA("Pessoa Fisica"),
    PESSOA_JURIDICA("Pessoa Juridica");

    private final String valor;
}