package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.exception.CartaCatalogoNaoEncontradaException;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AdicionarItemRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.LoteItemInventarioRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.LoteItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation.ValidadorItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Estado;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Idioma;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteItemInventarioService {

    private final ItemInventarioRepository itemInventarioRepository;
    private final CatalogoFacade catalogoFacade;

    private final List<ValidadorItemInventario> validadores;


    // Esse bulk upsert sofre do mesmo problema do upsert, porém temos uma nova ocorrência de N+1
    // Aqui precisamos evitar N+1 na busca das cartas do catálogo e na validação de duplicatas.
    // Além disso, caso o usuário envie a lista com 2 itens identicos precisamos validar para que as quantidades sejam somadas.
    @Transactional
    public LoteItemInventarioResponseDTO adicionarPorLote(String usuarioId, LoteItemInventarioRequestDTO request) {
        List<AdicionarItemRequestDTO> lista = request.lista();

        // Tratamos o primerio caso de N+1
        Set<UUID> uuidsDetalheCartas = request.lista()
                        .stream().map(item-> UUID.fromString(item.cartaCatalogoId()))
                        .collect(Collectors.toSet());

        Map<UUID, CartaCatalogoDTO> detalhesCartas = catalogoFacade.buscaPorConjuntoIdsMap(uuidsDetalheCartas);

        // Validações
        lista.forEach(item -> {
            CartaCatalogoDTO detalhesCarta = detalhesCartas.get(UUID.fromString(item.cartaCatalogoId()));
            if (detalhesCarta == null) {
                throw new CartaCatalogoNaoEncontradaException("Detalhes da carta não encontrados para o ID: " + item.cartaCatalogoId());
            }
            validadores.forEach(validador -> validador.validar(item, detalhesCarta));
        });
    }
}
