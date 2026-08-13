package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartaCatalogoScryfallDTO(
        String oracleId,
        String printId,
        String nome,
        String edicao,
        String imagemFrenteUrl,
        String imagemVersoUrl
) {
}
