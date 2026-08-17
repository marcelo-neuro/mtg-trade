package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions;

public class ScryfallMissingImageException extends RuntimeException {
    public ScryfallMissingImageException(String message) {
        super(message);
    }
}
