package br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto;

import br.com.marceloneuro.mtgtrade.oferta.internal.domain.StatusOferta;

import java.time.Instant;
import java.util.List;

public record OfertaResponseDTO(
    String id,
    String usuarioId,
    StatusOferta statusOferta,
    Instant ultimaModificacao,
    ItemOferecidoResponseDTO itemOferecido,
    List<ItemDesejadoResponseDTO> itensDesejados
) {

    public record ItemOferecidoResponseDTO(
            String itemInventarioId,
            String cartaCatalogoId,
            String idioma,
            String estado,
            String acabamento,
            String promo,
            String nomeCarta,
            String urlImagem,
            String siglaSet,
            String numeroColecionador
    ) {}

    public record ItemDesejadoResponseDTO(
            String id,
            String cartaCatalogoId,
            List<String> acabamentosAceitos,
            List<String> promosAceitas,
            List<String> idiomasAceitos,
            Integer quantidade,
            String estadoMinimo,
            String nomeCarta,
            String urlImagem,
            String siglaSet,
            String numeroColecionador
    ) {}
}
