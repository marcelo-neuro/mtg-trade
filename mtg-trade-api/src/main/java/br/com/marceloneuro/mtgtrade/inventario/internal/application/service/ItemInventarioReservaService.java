package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.ItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ItemNaoEncontradoException;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.exception.ReservaItemException;
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
            throw new ReservaItemException("Item já está reservado.");
        }

        if (itemOriginal.getQuantidade() < pedidoReserva.quantidade()) {
            throw new QuantidadeExcedenteException("Quantidade solicitada para reserva é maior que a quantidade presente no inventário.");
        }

        CartaCatalogoDTO detalhesCarta = catalogoFacade.obterPorId(itemOriginal.getCartaCatalogoId());

        ItemInventario itemSalvo = transferirSaldo(itemOriginal, pedidoReserva.quantidade(),  true, uuidUsuario);

        return new ItemInventarioResponseDTO(itemSalvo, detalhesCarta);
    }

    @Transactional
    public ItemInventarioResponseDTO liberarItem(PedidoReservaDTO pedidoLiberacao, String idUsuario) {
        UUID uuidCarta = UUID.fromString(pedidoLiberacao.itemInventarioUUID());
        UUID uuidUsuario = UUID.fromString(idUsuario);

        ItemInventario itemOriginal = itemInventarioRepository.findByIdAndUsuarioId(
                uuidCarta,
                uuidUsuario
        ).orElseThrow(() -> new ItemNaoEncontradoException("Item não existente no inventário do usuário."));

        if (!itemOriginal.getReservado()) {
            throw new ReservaItemException("Esse item já está liberado.");
        }

        if (pedidoLiberacao.quantidade() > itemOriginal.getQuantidade()) {
            throw new QuantidadeExcedenteException("Quantidade solicitada para liberação é maior que a quantidade presente no inventário.");
        }

        CartaCatalogoDTO detalhesCarta = catalogoFacade.obterPorId(itemOriginal.getCartaCatalogoId());

        ItemInventario itemSalvo = transferirSaldo(itemOriginal, pedidoLiberacao.quantidade(), false, uuidUsuario);

        return new ItemInventarioResponseDTO(itemSalvo, detalhesCarta);
    }

    private ItemInventario transferirSaldo(ItemInventario itemOriginal, Integer quantidade, Boolean destinoReserva, UUID uuidUsuario) {

        Optional<ItemInventario> itemDestinoOpt = itemInventarioRepository
                .findByUsuarioIdAndCartaCatalogoIdAndAcabamentoAndPromoAndEstadoAndIdiomaAndReservado(uuidUsuario, itemOriginal.getCartaCatalogoId(),
                        itemOriginal.getAcabamento(), itemOriginal.getPromo(), itemOriginal.getEstado(), itemOriginal.getIdioma(), destinoReserva);

        ItemInventario itemDestino;

        if (itemDestinoOpt.isPresent()) {
            itemDestino = itemDestinoOpt.get();

            itemOriginal.setQuantidade(itemOriginal.getQuantidade() - quantidade);
            itemDestino.setQuantidade(itemDestino.getQuantidade() + quantidade);
        } else {
            itemOriginal.setQuantidade(itemOriginal.getQuantidade() - quantidade);

            itemDestino = new ItemInventario();
            itemDestino.setUsuarioId(itemOriginal.getUsuarioId());
            itemDestino.setCartaCatalogoId(itemOriginal.getCartaCatalogoId());
            itemDestino.setAcabamento(itemOriginal.getAcabamento());
            itemDestino.setPromo(itemOriginal.getPromo());
            itemDestino.setEstado(itemOriginal.getEstado());
            itemDestino.setIdioma(itemOriginal.getIdioma());
            itemDestino.setQuantidade(quantidade);
            itemDestino.setReservado(destinoReserva);
        }

        // Caso a quantidade reservada seja igual à quantidade total, excluímos o registro original
        if (Integer.valueOf(0).equals(itemOriginal.getQuantidade())) {
            itemInventarioRepository.delete(itemOriginal);
        } else {
            // Caso a quantidade seja menor atualizamos o registro original.
            itemInventarioRepository.save(itemOriginal);
        }

        return itemInventarioRepository.save(itemDestino);
    }
}
