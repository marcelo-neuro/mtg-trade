package br.com.marceloneuro.mtgtrade.inventario.internal.facade;

import br.com.marceloneuro.mtgtrade.inventario.api.InventarioFacade;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.ItemInventarioDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.ItemInventarioReservaService;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemNaoEncontradoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemInventarioFacadeImpl implements InventarioFacade {

    private final ItemInventarioReservaService itemInventarioReservaService;
    private final ItemInventarioRepository itemInventarioRepository;

    @Override
    public void reservarItem(PedidoReservaDTO pedidoReserva, String usuarioID) {
        itemInventarioReservaService.reservarItem(pedidoReserva, usuarioID);
    }

    @Override
    public void liberarItem(PedidoReservaDTO pedidoLiberacao, String usuarioId) {
        itemInventarioReservaService.liberarItem(pedidoLiberacao, usuarioId);
    }

    @Override
    public ItemInventarioDTO buscaPorIdEUsuarioId(String itemId, String usuarioId) {
        return converteEntidadeParaDTO(itemInventarioRepository.findByIdAndUsuarioId(UUID.fromString(itemId), UUID.fromString(usuarioId))
                .orElseThrow(() -> new ItemNaoEncontradoException("Item não encontrado no inventário do usuário.")));

    }

    private ItemInventarioDTO converteEntidadeParaDTO(ItemInventario entidade) {
        return new ItemInventarioDTO(
                entidade.getId().toString(),
                entidade.getUsuarioId().toString(),
                entidade.getCartaCatalogoId().toString(),
                entidade.getAcabamento(),
                entidade.getPromo(),
                entidade.getQuantidade(),
                entidade.getEstado().toString(),
                entidade.getIdioma().toString(),
                entidade.getReservado()
        );
    }
}
