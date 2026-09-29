package br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class ItemNaoEncontradoException extends DominioException {
    public ItemNaoEncontradoException(String message) {
        super(
                message,
                "Item Não Encontrado",
                "IIV-001",
                TipoErroDominio.RECURSO_NAO_ENCONTRADO
        );
    }
}
