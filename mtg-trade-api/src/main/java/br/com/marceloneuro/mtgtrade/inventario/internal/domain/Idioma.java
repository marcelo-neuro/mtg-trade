package br.com.marceloneuro.mtgtrade.inventario.internal.domain;

import lombok.Getter;

@Getter
public enum Idioma {
    EN("Inglês"),
    PT("Português"),
    ES("Espanhol"),
    FR("Francês"),
    DE("Alemão"),
    IT("Italiano"),
    JA("Japonês"),
    KO("Coreano"),
    RU("Russo"),
    ZHS("Chinês Simplificado"),
    ZHT("Chinês Tradicional"),

    // Idioma fictício mais presente no jogo
    PH("Phyrexiano"),

    // Magic possui díversos idiomas que aparecem apenas numa coleção (Latim, Sânscrito, etc)
    OUTROS("Outros/Promocional");

    private final String descricao;

    Idioma(String descricao) {
        this.descricao = descricao;
    }

}
