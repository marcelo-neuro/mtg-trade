package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtributosFisicosRequest;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Idioma;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ValidarIdioma implements ValidadorItemInventario {
    @Override
    public void validar(AtributosFisicosRequest request, CartaCatalogoDTO cartaCatalogo) {
        boolean idiomaValido = Arrays.stream(Idioma.values())
                .anyMatch(idioma -> idioma.name().equalsIgnoreCase(request.idioma().trim()));

        if (!idiomaValido) {
            throw new IllegalArgumentException("Idioma não existente.");
        }
    }
}
