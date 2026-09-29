package br.com.marceloneuro.mtgtrade.catalogo.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.ExcecaoDominio;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class CartaCatalogoNaoEncontradaException extends ExcecaoDominio {

    public CartaCatalogoNaoEncontradaException(String mensagem) {
        super(
                mensagem,
                "Carta do Catálogo Não Encontrada.",
                "CC-001",
                TipoErroDominio.RECURSO_NAO_ENCONTRADO
        );
    }
}
