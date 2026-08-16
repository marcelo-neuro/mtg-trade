package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record CartaScryfallDTO(
        @JsonProperty("oracle_id")
        String oracleId,
        @JsonProperty("id")
        String id,
        @JsonProperty("name")
        String name,
        @JsonProperty("set_name")
        String setName,

        ImageUrisScryfallDTO imageUris,
        List<CardFaceScryfallDTO> cardFaces
) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        record ImageUrisScryfallDTO(
                @JsonProperty("normal")
                String normal
        ) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record CardFaceScryfallDTO(
                @JsonProperty("normal")
                ImageUrisScryfallDTO imageUris,

                @JsonProperty("mana_cost")
                String custoDeMana
        ) {
        }
}
