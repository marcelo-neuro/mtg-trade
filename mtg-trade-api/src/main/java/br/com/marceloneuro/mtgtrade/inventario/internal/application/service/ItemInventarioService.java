package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AdicionarItemRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AtualizarItemRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.ItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Estado;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Idioma;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemInventarioService {

    private final CatalogoFacade catalogoFacade;
    private final ItemInventarioRepository itemInventarioRepository;

    public ItemInventarioResponseDTO adicionarItem(String usuarioId, AdicionarItemRequestDTO request) {
        ItemInventario itemInventarioAdicionado = null;

        UUID uuidUsuario = UUID.fromString(usuarioId);
        UUID uuidCartaCatalogo = UUID.fromString(request.cartaCatalogoId());
        Estado estadoItem = Estado.valueOf(request.estado().trim().toUpperCase());
        Idioma idiomaItem = Idioma.valueOf(request.idioma().trim().toUpperCase());

        // Busca para saber se a carta adicionada já está no inventário do usuário.
        Optional<ItemInventario> itemInventarioBuscado = itemInventarioRepository
                .findByUsuarioIdAndCartaCatalogoIdAndAcabamentoAndPromoAndEstadoAndIdioma(uuidUsuario, uuidCartaCatalogo,
                        request.acabamento(), request.promo(), estadoItem, idiomaItem);

        // Caso o item esteja presente adicionamos a quantidade que o usuário solicitou no AdicionarItemRequestDTO
        if (itemInventarioBuscado.isPresent()) {
            itemInventarioAdicionado = itemInventarioBuscado.get();
            itemInventarioAdicionado.setQuantidade(itemInventarioAdicionado.getQuantidade() + request.quantidade());
        } else {
            // Caso o item não esteja presente criamos um registro
            itemInventarioAdicionado = new ItemInventario();
            itemInventarioAdicionado.setUsuarioId(uuidUsuario);
            itemInventarioAdicionado.setCartaCatalogoId(uuidCartaCatalogo);
            itemInventarioAdicionado.setAcabamento(request.acabamento());
            itemInventarioAdicionado.setPromo(request.promo());
            itemInventarioAdicionado.setQuantidade(request.quantidade());
            itemInventarioAdicionado.setEstado(estadoItem);
            itemInventarioAdicionado.setIdioma(idiomaItem);
        }

        ItemInventario itemSalvo = itemInventarioRepository.save(itemInventarioAdicionado);
        CartaCatalogoDTO cartaCatalogo = catalogoFacade.obterPorId(request.cartaCatalogoId());
        return new ItemInventarioResponseDTO(itemSalvo, cartaCatalogo);
    }

    public Page<ItemInventarioResponseDTO> listarItensUsuario(String usuarioId, Pageable pageable) {
        // TODO: Retornar lista paginada de itens pertencentes ao usuarioId
        return Page.empty();
    }

    public ItemInventarioResponseDTO buscarItemPorId(String usuarioId, String itemId) {
        // TODO: Buscar item, garantir que pertence ao usuarioId e mapear para DTO
        return null;
    }

    public ItemInventarioResponseDTO atualizarItem(String usuarioId, String itemId, AtualizarItemRequestDTO request) {
        // TODO: Validar pertencimento ao usuário e atualizar quantidade/estado
        return null;
    }

    public void removerItem(String usuarioId, String itemId) {
        // TODO: Validar pertencimento ao usuário e deletar o registro
    }
}
