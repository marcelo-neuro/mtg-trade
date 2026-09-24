package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAcabamento implements ValidadorItemInventario{
    @Override
    public void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo) {
        if (!cartaCatalogo.acabamentos().contains(request.acabamento().trim().toLowerCase())) {
            throw new IllegalArgumentException("A carta selecionada não pode ter esse tipo de acabamento");
        }
    }
}
