package br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto;

import br.com.marceloneuro.mtgtrade.oferta.internal.domain.StatusOferta;

import java.time.Instant;
import java.util.List;

public record OfertaResponseDTO(
    String id,
    String usuarioId,
    StatusOferta statusOferta,
    Instant criadaEm,
    ItemOferecidoResponseDTO itemOferecido,
    List<ItemDesejadoResponseDTO> itensDesejados
) {

    public record ItemOferecidoResponseDTO(
            String itemInventarioId,
            String cartaCatalogoId,
            String nomeCarta,
            String urlImagem,
            String siglaSet,
            String numeroColecionador,
            String idioma,
            String estado,
            String acabamento,
            String promo
    ) {}

    public record ItemDesejadoResponseDTO(
            String id,
            String cartaCatalogoId,
            String nomeCarta,
            String urlImagem,
            String siglaSet,
            String numeroColecionador,
            Integer quantidade,
            List<String> acabamentosAceitos,
            List<String> promosAceitas,
            List<String> idiomasAceitos,
            String estadoMinimo
    ) {}
}
