package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosValidacaoRequest;

public interface ValidadorItemInventario {
    void validar(AtributosValidacaoRequest request, CartaCatalogoDTO cartaCatalogo);
}
