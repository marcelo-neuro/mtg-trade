package br.com.marceloneuro.mtgtrade.catalogo.api;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;

import java.util.UUID;

public interface CatalogoFacade {
    CartaCatalogoDTO obterPorPrintId(String printId);
    CartaCatalogoDTO obterPorId(UUID id);
}
