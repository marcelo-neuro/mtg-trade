package br.com.marceloneuro.mtgtrade.catalogo.api.dto;

public record ChaveBuscaCatalogoDTO(
        String edicao,
        String numeroColecionador
) {

    public ChaveBuscaCatalogoDTO {
        edicao = edicao != null ? edicao : "";
        numeroColecionador = numeroColecionador != null ? numeroColecionador.toUpperCase() : "";
    }
}
