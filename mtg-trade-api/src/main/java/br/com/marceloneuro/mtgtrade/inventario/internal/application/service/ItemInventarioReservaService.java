package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.ItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemNaoEncontradoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemReservadoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.QuantidadeExcedenteException;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Para criar uma reserva vamos subtrair a quantidade reservada da quantidade presente no inventário,
// então criamos um registro com a flag reservado = true, que irá travar a linha.
// Caso a quantidade reservada seja igual à quantidade no inventário nós excluíremos 
@Service
@RequiredArgsConstructor
public class ItemInventarioReservaService {

    private final ItemInventarioRepository itemInventarioRepository;


    public ItemInventarioResponseDTO reservarItem(PedidoReservaDTO pedidoReserva, String idUsuario) {

        ItemInventario itemInventario = itemInventarioRepository.findByIdAndUsuarioId(
                UUID.fromString(pedidoReserva.itemInventarioUUID()),
                UUID.fromString(idUsuario)
                ).orElseThrow(() -> new ItemNaoEncontradoException("Item não existente no inventário do usuário."));

        if (itemInventario.getReservado()) {
            throw new ItemReservadoException("Item já está reservado.");
        }

        if (itemInventario.getQuantidade() < pedidoReserva.quantidade()) {
            throw new QuantidadeExcedenteException("Quantidade solicitada para reserva é maior que a quantidade presente no inventário.");
        }


    }
}
