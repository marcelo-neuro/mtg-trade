package br.com.marceloneuro.mtgtrade.catalogo.internal.facade;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.exception.CartaCatalogoNaoEncontradaException;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.CatalogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CatalogoFacadeImpl implements CatalogoFacade {

    private final CatalogoRepository catalogoRepository;

    @Override
    public CartaCatalogoDTO obterPorPrintId(String printId) {
        return catalogoRepository.findByPrintId(printId)
                .map(cartaCatalogo -> new CartaCatalogoDTO(
                        cartaCatalogo.getId().toString(),
                        cartaCatalogo.getOracleId(),
                        cartaCatalogo.getPrintId(),
                        cartaCatalogo.getNome(),
                        cartaCatalogo.getEdicao(),
                        cartaCatalogo.getAcabamentos(),
                        cartaCatalogo.getTiposPromo(),
                        cartaCatalogo.getIsPromo(),
                        cartaCatalogo.getImagemFrenteUrl(),
                        cartaCatalogo.getImagemVersoUrl()
                ))
                .orElseThrow(() -> new CartaCatalogoNaoEncontradaException("Carta não contrada por Print Id."));
    }

    @Override
    public CartaCatalogoDTO obterPorId(UUID id) {
        return catalogoRepository.findById(id)
                .map(cartaCatalogo -> new CartaCatalogoDTO(
                        cartaCatalogo.getId().toString(),
                        cartaCatalogo.getOracleId(),
                        cartaCatalogo.getPrintId(),
                        cartaCatalogo.getNome(),
                        cartaCatalogo.getEdicao(),
                        cartaCatalogo.getAcabamentos(),
                        cartaCatalogo.getTiposPromo(),
                        cartaCatalogo.getIsPromo(),
                        cartaCatalogo.getImagemFrenteUrl(),
                        cartaCatalogo.getImagemVersoUrl()
                ))
                .orElseThrow(() -> new CartaCatalogoNaoEncontradaException("Carta não encontrada pelo Id."));
    }

    @Override
    public List<CartaCatalogoDTO> buscaPorConjuntoIds(Iterable<UUID> conjuntoIds) {
        return catalogoRepository.findAllById(conjuntoIds)
                .stream().map(cartaCatalogo -> new CartaCatalogoDTO(
                        cartaCatalogo.getId().toString(),
                        cartaCatalogo.getOracleId(),
                        cartaCatalogo.getPrintId(),
                        cartaCatalogo.getNome(),
                        cartaCatalogo.getEdicao(),
                        cartaCatalogo.getAcabamentos(),
                        cartaCatalogo.getTiposPromo(),
                        cartaCatalogo.getIsPromo(),
                        cartaCatalogo.getImagemFrenteUrl(),
                        cartaCatalogo.getImagemVersoUrl()
                ))
                .toList();
    }

    @Override
    public Map<UUID, CartaCatalogoDTO> buscaPorConjuntoIdsMap(Iterable<UUID> conjuntoIds) {
        return buscaPorConjuntoIds(conjuntoIds)
                .stream().collect(Collectors.toMap(
                        cartaCatalogoDTO -> UUID.fromString(cartaCatalogoDTO.id()),
                        detalhesCarta -> detalhesCarta
                ));
    }
}
