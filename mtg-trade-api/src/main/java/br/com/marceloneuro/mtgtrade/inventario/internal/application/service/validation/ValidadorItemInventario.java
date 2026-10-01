package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;

public interface ValidadorItemInventario {
    void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo);
}
