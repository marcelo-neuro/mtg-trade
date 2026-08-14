package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
class ScryfallParser {

    private final ObjectMapper objectMapper;

    public CartaScryfallDTO converteLinhaJsonEmCartaCatalogoScryfallDTO(String json) {
        return objectMapper.readValue(json, CartaScryfallDTO.class);
    }
}
