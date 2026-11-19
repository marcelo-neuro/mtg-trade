package br.com.marceloneuro.mtgtrade.inventario.api.dto;

public record ItemInventarioDTO(
        String id,
        String usuarioId,
        String cartaCatalogoId,
        String acabamento,
        String promo,
        Integer quantidade,
        String estado,
        String idioma,
        Boolean reservado
) {}
