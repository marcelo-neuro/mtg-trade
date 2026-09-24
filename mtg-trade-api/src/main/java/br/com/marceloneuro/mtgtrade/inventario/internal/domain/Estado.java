package br.com.marceloneuro.mtgtrade.inventario.internal.domain;

import lombok.Getter;

@Getter
// Estado de conservação em que a carta se encontra
public enum Estado {
    MINT(5),
    NEAR_MIN(4),
    SLIGHTLY_PLAYED(3),
    MODERATELY_PLAYED(2),
    HEAVILY_PLAYED(1),
    DAMAGED(0);

    private int peso;

    Estado (int peso) {
        this.peso = peso;
    }

}
