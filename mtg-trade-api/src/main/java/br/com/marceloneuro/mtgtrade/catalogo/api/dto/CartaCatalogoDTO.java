package br.com.marceloneuro.mtgtrade.catalogo.api.dto;

public record CartaCatalogoDTO(
        String id,
        String oracleId,
        String printId,
        String nome,
        String edicao,
        String acabamentos,
        String tiposPromo,
        Boolean isPromo,
        String imagemFrenteUrl,
        String imagemVersoUrl
) {
}
