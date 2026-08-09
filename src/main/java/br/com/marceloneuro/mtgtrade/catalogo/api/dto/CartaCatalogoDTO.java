package br.com.marceloneuro.mtgtrade.catalogo.api.dto;

public record CartaCatalogoDTO(
        String oracleId,
        String printId,
        String nome,
        String edicao,
        String imagemUrl
) {
}
