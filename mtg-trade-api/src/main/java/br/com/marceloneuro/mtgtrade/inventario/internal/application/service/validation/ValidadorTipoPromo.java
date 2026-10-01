package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidadorTipoPromo implements ValidadorItemInventario{
    @Override
    public void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo) {
        String promoRequisitada = request.promo().trim().toLowerCase();
        boolean promoDeclarada = !promoRequisitada.isEmpty();

        if (!promoDeclarada) {
            return;
        }

        if (!cartaCatalogo.isPromo()) {
            throw new IllegalArgumentException("A carta selecionada não possui opção promocional.");
        }

        if (!cartaCatalogo.tiposPromo().contains(promoRequisitada)) {
            throw new IllegalArgumentException("Opção promocional inválida para a carta selecionada.");
        }
    }
}
