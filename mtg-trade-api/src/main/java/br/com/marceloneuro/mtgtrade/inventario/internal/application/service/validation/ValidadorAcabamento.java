package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;

public class ValidadorAcabamento implements ValidadorItemInventario{
    @Override
    public void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo) {
        if (!cartaCatalogo.acabamentos().contains(request.acabamento())) {
            throw new IllegalArgumentException("A carta selecionada não pode ter esse tipo de acabamento");
        }
    }
}
