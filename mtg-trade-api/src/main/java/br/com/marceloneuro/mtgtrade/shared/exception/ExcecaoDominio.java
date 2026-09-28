package br.com.marceloneuro.mtgtrade.shared.exception;

public abstract class ExcecaoDominio extends RuntimeException {

    private String codigo;
    private String mensagem;
    private String titulo;
    private TipoErroDominio erroDominio;
}
