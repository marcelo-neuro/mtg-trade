package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallLayoutException;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallMissingImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
class ScryfallParser {

    private final ObjectMapper objectMapper;

    public CartaCatalogo converteLinhaJsonEmCartaCatalogo(String json) {
        CartaScryfallDTO cartaScryfallDto = objectMapper.readValue(json, CartaScryfallDTO.class);

        if ("missing".equals(cartaScryfallDto.imageStatus())) {
            throw new ScryfallMissingImageException("Carta sem imagem não é suportada.");
        }
        if ("art_series".equals(cartaScryfallDto.layout())) {
            throw new ScryfallLayoutException("Layout de carta não suportado.");
        }

        CartaCatalogo cartaCatalogoConvertida = new CartaCatalogo();

        cartaCatalogoConvertida.setOracleId(cartaScryfallDto.oracleId());
        cartaCatalogoConvertida.setPrintId(cartaScryfallDto.id());
        cartaCatalogoConvertida.setNome(cartaScryfallDto.name());
        cartaCatalogoConvertida.setEdicao(cartaScryfallDto.setName());
        cartaCatalogoConvertida.setIsPromo(cartaScryfallDto.promo());

        cartaCatalogoConvertida.setAcabamentos(converteOpcoesDetalhamentoParaString(cartaScryfallDto.finishes()));
        cartaCatalogoConvertida.setTiposPromo(converteOpcoesDetalhamentoParaString(cartaScryfallDto.promoTypes()));

        // Carta de dupla face
        if (cartaScryfallDto.imageUris() == null) {
            converteCartaDuplaFace(cartaCatalogoConvertida, cartaScryfallDto.cardFaces());
            return  cartaCatalogoConvertida;
        }

        cartaCatalogoConvertida.setImagemFrenteUrl(cartaScryfallDto.imageUris().normal());

        return cartaCatalogoConvertida;
    }

    private void converteCartaDuplaFace(CartaCatalogo cartaCatalogoConvertida, List<CartaScryfallDTO.CardFaceScryfallDTO> cardFaceScryfallDto) {
        if (cardFaceScryfallDto == null || cardFaceScryfallDto.isEmpty()) {
            throw new ScryfallMissingImageException("Carta sem imagem não pode ser cadastrada no catalogo.");
        }

        if (cartaCatalogoConvertida.getOracleId() == null) {
            cartaCatalogoConvertida.setOracleId(cardFaceScryfallDto.getFirst().oracleId());
        }

        CartaScryfallDTO.CardFaceScryfallDTO faceFrente = cardFaceScryfallDto.getFirst();
        if (faceFrente.imageUris() != null) {
            cartaCatalogoConvertida.setImagemFrenteUrl(faceFrente.imageUris().normal());
        }

        CartaScryfallDTO.CardFaceScryfallDTO faceVerso = cardFaceScryfallDto.getLast();
        if (faceVerso != null) {
            cartaCatalogoConvertida.setImagemVersoUrl(cardFaceScryfallDto.getLast().imageUris().normal());
        }
    }

    private String converteOpcoesDetalhamentoParaString(List<String> opcoes) {
        if (opcoes == null) {
            return null;
        }

        StringJoiner joiner = new StringJoiner(",");

        for (String opcao : opcoes) {
            joiner.add(opcao);
        }

        return joiner.toString();
    }
}
