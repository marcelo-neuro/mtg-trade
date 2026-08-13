package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record CardFaceScryfallDTO(
        @JsonProperty("normal")
        ImageUrisScryfallDTO imageUris,

        @JsonProperty("mana_cost")
        String custoDeMana
) {
}
