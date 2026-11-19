package br.com.marceloneuro.mtgtrade.oferta.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class OfertaNaoEncontradaException extends DominioException {
    public OfertaNaoEncontradaException(String message) {
        super(
                message,
                "Oferta não encontrada",
                "OFR-001",
                TipoErroDominio.RECURSO_NAO_ENCONTRADO
        );
    }
}
