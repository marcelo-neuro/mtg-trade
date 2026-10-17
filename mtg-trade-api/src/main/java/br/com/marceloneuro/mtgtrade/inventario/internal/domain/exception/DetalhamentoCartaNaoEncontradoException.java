package br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class DetalhamentoCartaNaoEncontradoException extends DominioException {
    public DetalhamentoCartaNaoEncontradoException(String message, String titulo, String codigo, TipoErroDominio erroDominio) {
        super(
                message,
                "Detalhamento da Carta Não Encontrado",
                "IIV-003",
                TipoErroDominio.RECURSO_NAO_ENCONTRADO
        );
    }
}
