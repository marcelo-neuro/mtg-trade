package br.com.marceloneuro.mtgtrade.inventario.api;

import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;

public interface InventarioFacade {

    void reservarItem(PedidoReservaDTO pedidoReserva, String idUsuario);
    void liberarItem(PedidoReservaDTO pedidoLiberacao, String idUsuario);
}
