package br.com.marceloneuro.mtgtrade.catalogo.api;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;

public interface CatalogoFacade {
    CartaCatalogoDTO obterPorPrintId(String printId);
}
