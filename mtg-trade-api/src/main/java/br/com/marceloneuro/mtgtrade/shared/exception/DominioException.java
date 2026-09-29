package br.com.marceloneuro.mtgtrade.shared.exception;

import lombok.Getter;

@Getter
public abstract class DominioException extends RuntimeException {

    private final String codigo;
    private final String titulo;
    private final TipoErroDominio erroDominio;

    public DominioException(String message, String titulo, String codigo, TipoErroDominio erroDominio) {
        super(message);
        this.titulo = titulo;
        this.codigo = codigo;
        this.erroDominio = erroDominio;
    }
}
