package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
class ScryfallParser {

    private final ObjectMapper objectMapper;

    public CartaCatalogo converteLinhaJsonEmCartaCatalogo(String json) {
        CartaScryfallDTO cartaScryfallDto = objectMapper.readValue(json, CartaScryfallDTO.class);

        CartaCatalogo cartaCatalogoConvertida = new CartaCatalogo();

        cartaCatalogoConvertida.setOracleId(cartaScryfallDto.oracleId());
        cartaCatalogoConvertida.setPrintId(cartaScryfallDto.id());
        cartaCatalogoConvertida.setNome(cartaScryfallDto.name());
        cartaCatalogoConvertida.setEdicao(cartaScryfallDto.setName());

        // Carta de dupla face
        if (cartaScryfallDto.imageUris() == null) {
            converteCartaDuplaFace(cartaCatalogoConvertida, cartaScryfallDto.cardFaces());
            return  cartaCatalogoConvertida;
        }

        cartaCatalogoConvertida.setImagemFrenteUrl(cartaScryfallDto.imageUris().normal());

        return cartaCatalogoConvertida;
    }

    private void converteCartaDuplaFace(CartaCatalogo cartaCatalogoConvertida, List<CartaScryfallDTO.CardFaceScryfallDTO> cardFaceScryfallDto) {
        cardFaceScryfallDto.forEach(face -> {
            if (face.custoDeMana().isBlank()) {
                cartaCatalogoConvertida.setImagemVersoUrl(face.imageUris().normal());
            }
            if (!face.custoDeMana().isBlank()) {
                cartaCatalogoConvertida.setImagemFrenteUrl(face.imageUris().normal());
            }
        });
    }
}
