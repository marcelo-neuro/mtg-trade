package br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class ViolacaoEstadoFisicoException extends DominioException {
    public ViolacaoEstadoFisicoException(String message) {
        super(
                message,
                "Violação de estado físico",
                "IIV-002",
                TipoErroDominio.VIOLACAO_REGRA_NEGOCIO
        );
    }
}
