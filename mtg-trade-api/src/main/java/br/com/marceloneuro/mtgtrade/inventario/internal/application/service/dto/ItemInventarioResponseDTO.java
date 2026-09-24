package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;

public record ItemInventarioResponseDTO(

        // Atributos referentes a carta física
        String id,
        String acabamento,
        String promo,
        Integer quantidade,
        String estado,
        String idioma,

        // Atributos referentes às cartas do catálogo
        String cartaCatalogoId,
        String nome,
        String edicao,
        String imagemFrenteUrl
) {

    public ItemInventarioResponseDTO(ItemInventario entidade, CartaCatalogoDTO cartaCatalogo) {
        this(
                entidade.getId().toString(),
                entidade.getAcabamento(),
                entidade.getPromo(),
                entidade.getQuantidade(),
                entidade.getEstado().toString(),
                entidade.getIdioma().getDescricao(),

                cartaCatalogo.id(),
                cartaCatalogo.nome(),
                cartaCatalogo.edicao(),
                cartaCatalogo.imagemFrenteUrl()
        );
    }
}
