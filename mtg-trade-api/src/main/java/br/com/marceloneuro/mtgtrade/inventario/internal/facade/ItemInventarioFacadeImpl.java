package br.com.marceloneuro.mtgtrade.inventario.internal.facade;

import br.com.marceloneuro.mtgtrade.inventario.api.InventarioFacade;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.ItemInventarioReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemInventarioFacadeImpl implements InventarioFacade {

    private final ItemInventarioReservaService itemInventarioReservaService;

    @Override
    public void reservarItem(PedidoReservaDTO pedidoReserva, String idUsuario) {
        itemInventarioReservaService.reservarItem(pedidoReserva, idUsuario);
    }

    @Override
    public void liberarItem(PedidoReservaDTO pedidoLiberacao, String idUsuario) {
        itemInventarioReservaService.liberarItem(pedidoLiberacao, idUsuario);
    }
}
