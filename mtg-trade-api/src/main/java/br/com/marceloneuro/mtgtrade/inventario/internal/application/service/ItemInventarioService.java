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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemInventarioService {

    private final CatalogoFacade catalogoFacade;
    private final ItemInventarioRepository itemInventarioRepository;

    // Esse trecho é responsável por adicionar um item ao inventário de um usuário,
    // caso o item já exista ele deve somar a quantidade ao item já criado na tabela.
    @Transactional
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
        CartaCatalogoDTO cartaCatalogo = catalogoFacade.obterPorId(uuidCartaCatalogo);
        return new ItemInventarioResponseDTO(itemSalvo, cartaCatalogo);
    }

    // Como estamos retornando ItemInventarioResponseDTO, que pede por um detalhamento da carta esse tipo de consulta gera um problema de N+1
    // Para contornarmos isso iremos buscar todas as cartas do catalogo de uma vez, assim evitando idas desnecessárias ao banco de dados.
    public Page<ItemInventarioResponseDTO> listarItensUsuario(String usuarioId, Pageable pageable) {
        // TODO: Retornar lista paginada de itens pertencentes ao usuarioId
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ItemInventarioResponseDTO buscarItemPorId(String usuarioId, String itemId) {
        ItemInventario itemInventario = itemInventarioRepository.findByIdAndUsuarioId(UUID.fromString(itemId), UUID.fromString(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Item não existente no inventário do usuário."));

        CartaCatalogoDTO detalhesCarta = catalogoFacade.obterPorId(itemInventario.getCartaCatalogoId());


        return new ItemInventarioResponseDTO(itemInventario, detalhesCarta);
    }

    // Esse trecho é responsável por modificar um item, na prática, isso pode gerar alguns problemas,
    // uma vez que o usuário pode modificar o item e ele pode colidir com a constraint unique, para evitar isso orquestraremos um update e delete
    // se o estado final do registro modificado causar colisão nós fazemos a adição no item já existente.
    @Transactional
    public ItemInventarioResponseDTO atualizarItem(String usuarioId, String itemId, AtualizarItemRequestDTO request) {
        UUID uuidItem = UUID.fromString(itemId);
        UUID uuidUsuario = UUID.fromString(usuarioId);
        ItemInventario itemOriginal = itemInventarioRepository.findByIdAndUsuarioId(uuidItem, uuidUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Item não existente no inventário desse usuário."));

        Estado estadoItem = Estado.valueOf(request.estado().trim().toUpperCase());
        Idioma idiomaItem = Idioma.valueOf(request.idioma().trim().toUpperCase());


        // Busca para saber se já existe um registro físico desse item.
        Optional<ItemInventario> destinoOpt = itemInventarioRepository
                .findByUsuarioIdAndCartaCatalogoIdAndAcabamentoAndPromoAndEstadoAndIdioma(uuidUsuario, itemOriginal.getCartaCatalogoId(),
                        request.acabamento(), request.promo(), estadoItem, idiomaItem);

        CartaCatalogoDTO cartaCatalogo = catalogoFacade.obterPorId(itemOriginal.getCartaCatalogoId());

        ItemInventario itemSalvo;
        // Caso o item exista nós verificamos se eles não são o mesmo registro (ID)
        // Se não forem apagamos o original e adicionamos a quantidade ao novo registro.
        if (destinoOpt.isPresent()) {
            ItemInventario destino = destinoOpt.get();

            if (destino.equals(itemOriginal)) {
                itemSalvo = itemOriginal;
                itemSalvo.setQuantidade(request.quantidade());
            } else {
                itemInventarioRepository.delete(itemOriginal);
                destino.setQuantidade(destino.getQuantidade() + request.quantidade());
                itemSalvo = destino;
            }
        } else {
            // Caso o item não crie duplicata, apenas seguimos com o CRUD padrão
            itemSalvo = itemOriginal;
            itemSalvo.setAcabamento(request.acabamento());
            itemSalvo.setPromo(request.promo());
            itemSalvo.setQuantidade(request.quantidade());
            itemSalvo.setEstado(estadoItem);
            itemSalvo.setIdioma(idiomaItem);
        }

         itemSalvo = itemInventarioRepository.save(itemSalvo);
        return new ItemInventarioResponseDTO(itemSalvo, cartaCatalogo);
    }

    @Transactional
    public void removerItem(String usuarioId, String itemId) {
        ItemInventario itemInventario = itemInventarioRepository.findByIdAndUsuarioId(UUID.fromString(itemId), UUID.fromString(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Item não existente no inventário do usuário."));

        itemInventarioRepository.delete(itemInventario);
    }
}
