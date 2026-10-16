package br.com.marceloneuro.mtgtrade.inventario.internal.domain;

import lombok.Getter;

@Getter
// Estado de conservação em que a carta se encontra
public enum Estado {
    M(5, "Mint"),
    NM(4, "Near Mint"),
    SP(3, "Slightly Played"),
    MP(2, "Moderately Played"),
    HP(1, "Heavily Played"),
    DM(0, "Damaged");

    private int peso;
    private String descricao;

    Estado (int peso, String descricao) {
        this.peso = peso;
        this.descricao = descricao;
    }

}
