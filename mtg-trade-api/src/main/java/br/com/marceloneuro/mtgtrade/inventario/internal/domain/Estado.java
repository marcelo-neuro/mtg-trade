package br.com.marceloneuro.mtgtrade.inventario.internal.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
// Estado de conservação em que a carta se encontra
public enum Estado {
    M(5, "Mint"),
    NM(4, "Near Mint"),
    SP(3, "Slightly Played"),
    MP(2, "Moderately Played"),
    HP(1, "Heavily Played"),
    DM(0, "Damaged");

    private final static Set<String> VALORES_VALIDOS = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private int peso;
    private String descricao;

    Estado (int peso, String descricao) {
        this.peso = peso;
        this.descricao = descricao;
    }

    public static boolean isValido(String valor) {
        return valor != null && VALORES_VALIDOS.contains(valor.toUpperCase());
    }

}
