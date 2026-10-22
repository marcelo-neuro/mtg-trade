package br.com.marceloneuro.mtgtrade.catalogo.api;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.ChaveBuscaCatalogoDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CatalogoFacade {
    CartaCatalogoDTO obterPorPrintId(String printId);
    CartaCatalogoDTO obterPorId(UUID id);
    List<CartaCatalogoDTO> buscaPorConjuntoIds(Iterable<UUID> conjuntoIds);
    Map<UUID, CartaCatalogoDTO> buscaPorConjuntoIdsMap(Iterable<UUID> conjuntoIds);

    // Busca a prévia das cartas do catálogo a partir de edições e números de colecionador fornecidos
    Map<ChaveBuscaCatalogoDTO, CartaCatalogoDTO> buscarPorConjuntoEdicaoENumeroMap(Set<String> edicoes,
                                                                                   Set<String> numerosColecionador);
}
