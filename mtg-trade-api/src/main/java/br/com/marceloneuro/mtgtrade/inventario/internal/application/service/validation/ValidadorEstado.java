package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Estado;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ViolacaoEstadoFisicoException;

public class ValidadorEstado implements ValidadorItemInventario {
    @Override
    public void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo) {
        if (!Estado.isValido(request.estado())) {
            throw new ViolacaoEstadoFisicoException("Estado da carta inválido.");
        }
    }
}
