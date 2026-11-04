package br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class QuantidadeExcedenteException extends DominioException {
    public QuantidadeExcedenteException(String message) {
        super(
                message,
                "Quantidade solicitada excede a quantidade de itens presentes.",
                "IIV-005",
                TipoErroDominio.VIOLACAO_REGRA_NEGOCIO
        );
    }
}
