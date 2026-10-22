package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallLayoutException;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallMissingImageException;
import br.com.marceloneuro.mtgtrade.shared.util.HashUtil;
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
        cartaCatalogoConvertida.setSiglaEdicao(cartaScryfallDto.set().toUpperCase());
        cartaCatalogoConvertida.setNumeroColecionador(cartaScryfallDto.collectorNumber());
        cartaCatalogoConvertida.setIsPromo(cartaScryfallDto.promo());

        cartaCatalogoConvertida.setAcabamentos(converteOpcoesDetalhamentoParaString(cartaScryfallDto.finishes()));
        cartaCatalogoConvertida.setTiposPromo(converteOpcoesDetalhamentoParaString(cartaScryfallDto.promoTypes()));

        // Carta de dupla face
        if (cartaScryfallDto.imageUris() == null) {
            converteCartaDuplaFace(cartaCatalogoConvertida, cartaScryfallDto.cardFaces());
        } else {
            cartaCatalogoConvertida.setImagemFrenteUrl(cartaScryfallDto.imageUris().normal());
        }

        // Converte a entidade já criada para uma String que será utilizada para gerar o hash da carta, a fim de verificar se houve alteração na carta.
        String baseHash = (cartaCatalogoConvertida.getNome() != null ? cartaCatalogoConvertida.getNome() : "") + "|" +
                (cartaCatalogoConvertida.getEdicao() != null ? cartaCatalogoConvertida.getEdicao() : "") + "|" +
                (cartaCatalogoConvertida.getSiglaEdicao() != null ? cartaCatalogoConvertida.getSiglaEdicao() : "") + "|" +
                (cartaCatalogoConvertida.getImagemFrenteUrl() != null ? cartaCatalogoConvertida.getImagemFrenteUrl() : "") + "|" +
                (cartaCatalogoConvertida.getImagemVersoUrl() != null ? cartaCatalogoConvertida.getImagemVersoUrl() : "") + "|" +
                (cartaCatalogoConvertida.getAcabamentos() != null ? cartaCatalogoConvertida.getAcabamentos() : "") + "|" +
                (cartaCatalogoConvertida.getTiposPromo() != null ? cartaCatalogoConvertida.getTiposPromo() : "") + "|" +
                (cartaCatalogoConvertida.getNumeroColecionador() != null ? cartaCatalogoConvertida.getNumeroColecionador() : "");

        cartaCatalogoConvertida.setDataHash(HashUtil.gerarCheckSum(baseHash));

        return  cartaCatalogoConvertida;
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
