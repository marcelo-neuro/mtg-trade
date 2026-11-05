package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.ItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemNaoEncontradoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemReservadoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.QuantidadeExcedenteException;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

// Para criar uma reserva vamos subtrair a quantidade reservada da quantidade presente no inventário,
// então criamos um registro com a flag reservado = true, que irá travar a linha.
// Caso a quantidade reservada seja igual à quantidade no inventário nós excluíremos
@Service
@RequiredArgsConstructor
public class ItemInventarioReservaService {

    private final ItemInventarioRepository itemInventarioRepository;
    private final CatalogoFacade catalogoFacade;


    @Transactional
    public ItemInventarioResponseDTO reservarItem(PedidoReservaDTO pedidoReserva, String idUsuario) {
        UUID uuidCarta = UUID.fromString(pedidoReserva.itemInventarioUUID());
        UUID uuidUsuario = UUID.fromString(idUsuario);


        ItemInventario itemOriginal = itemInventarioRepository.findByIdAndUsuarioId(
                uuidCarta,
                uuidUsuario
                ).orElseThrow(() -> new ItemNaoEncontradoException("Item não existente no inventário do usuário."));

        if (itemOriginal.getReservado()) {
            throw new ItemReservadoException("Item já está reservado.");
        }

        if (itemOriginal.getQuantidade() < pedidoReserva.quantidade()) {
            throw new QuantidadeExcedenteException("Quantidade solicitada para reserva é maior que a quantidade presente no inventário.");
        }

        CartaCatalogoDTO detalhesCarta = catalogoFacade.obterPorId(itemOriginal.getCartaCatalogoId());

        Optional<ItemInventario> itemDestinoOpt = itemInventarioRepository
                .findByUsuarioIdAndCartaCatalogoIdAndAcabamentoAndPromoAndEstadoAndIdiomaAndReservado(uuidUsuario, itemOriginal.getCartaCatalogoId(),
                        itemOriginal.getAcabamento(), itemOriginal.getPromo(), itemOriginal.getEstado(), itemOriginal.getIdioma(), true);

        ItemInventario itemSalvo;


        if (itemDestinoOpt.isPresent()) {
            ItemInventario destino = itemDestinoOpt.get();

            itemOriginal.setQuantidade(itemOriginal.getQuantidade() - pedidoReserva.quantidade());
            destino.setQuantidade(destino.getQuantidade() + pedidoReserva.quantidade());
            itemSalvo = destino;
        } else {
            itemOriginal.setQuantidade(itemOriginal.getQuantidade() - pedidoReserva.quantidade());

            itemSalvo = new ItemInventario();
            itemSalvo.setUsuarioId(itemOriginal.getUsuarioId());
            itemSalvo.setCartaCatalogoId(itemOriginal.getCartaCatalogoId());
            itemSalvo.setAcabamento(itemOriginal.getAcabamento());
            itemSalvo.setPromo(itemOriginal.getPromo());
            itemSalvo.setEstado(itemOriginal.getEstado());
            itemSalvo.setIdioma(itemOriginal.getIdioma());
            itemSalvo.setQuantidade(pedidoReserva.quantidade());
            itemSalvo.setReservado(true);
        }

        // Caso a quantidade reservada seja igual à quantidade total, excluímos o registro original
        if (Integer.valueOf(0).equals(itemOriginal.getQuantidade())) {
            itemInventarioRepository.delete(itemOriginal);
        } else {
        // Caso a quantidade seja menor atualizamos o registro original.
            itemInventarioRepository.save(itemOriginal);
        }

        itemSalvo = itemInventarioRepository.save(itemSalvo);

        return new ItemInventarioResponseDTO(itemSalvo, detalhesCarta);
    }
}
