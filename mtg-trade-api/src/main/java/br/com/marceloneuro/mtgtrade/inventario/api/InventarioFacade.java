package br.com.marceloneuro.mtgtrade.inventario.api;

import br.com.marceloneuro.mtgtrade.inventario.api.dto.ItemInventarioDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;

public interface InventarioFacade {

    ItemInventarioDTO reservarItem(PedidoReservaDTO pedidoReserva, String usuarioID);
    ItemInventarioDTO liberarItem(PedidoReservaDTO pedidoLiberacao, String usarioID);
    ItemInventarioDTO buscaPorIdEUsuarioId(String itemId, String usuarioId);
}
