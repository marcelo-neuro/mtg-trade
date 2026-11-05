package br.com.marceloneuro.mtgtrade.inventario.api.dto;

public record PedidoReservaDTO(
        String itemInventarioUUID,
        Integer quantidade
) {
}
