package br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class ReservaItemException extends DominioException {
    public ReservaItemException(String message) {
        super(
                message,
                "Item reservado",
                "IIV-004",
                TipoErroDominio.VIOLACAO_REGRA_NEGOCIO
        );
    }
}
