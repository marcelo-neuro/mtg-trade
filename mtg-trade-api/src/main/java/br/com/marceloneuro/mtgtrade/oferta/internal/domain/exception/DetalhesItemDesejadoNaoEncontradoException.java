package br.com.marceloneuro.mtgtrade.oferta.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class DetalhesItemDesejadoNaoEncontradoException extends DominioException {
    public DetalhesItemDesejadoNaoEncontradoException(String message) {
        super(
                message,
                "Detalhamento do Item Desejado Não Encontrado",
                "OFR",
                TipoErroDominio.RECURSO_NAO_ENCONTRADO
        );
    }
}
