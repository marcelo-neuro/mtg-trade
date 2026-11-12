package br.com.marceloneuro.mtgtrade.oferta.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.InventarioFacade;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.ItemInventarioDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto.CriaOfertaRequestDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto.OfertaResponseDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.EstadoMinimo;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.ItemDesejado;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.Oferta;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.StatusOferta;
import br.com.marceloneuro.mtgtrade.oferta.internal.infrastructure.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final InventarioFacade inventarioFacade;
    private final CatalogoFacade catalogoFacade;

    // Para criar a oferta de um item precisamos lockar a entidade no banco através da flag de reserva.
    // Após isso atribuimos os itens desejados à oferta
    // Por fim buscamos os detalhes das cartas para popular o DTO de response
    @Transactional
    public OfertaResponseDTO criaOferta(CriaOfertaRequestDTO request, String usuarioId) {

        PedidoReservaDTO pedidoReserva = new PedidoReservaDTO(request.itemInventarioOfertadoId(),
                request.quantidade());

        inventarioFacade.reservarItem(pedidoReserva, usuarioId);

        Oferta oferta = new Oferta();
        oferta.setUsuarioId(UUID.fromString(usuarioId));
        oferta.setItemOferecido(UUID.fromString(request.itemInventarioOfertadoId()));
        oferta.setQuantidade(request.quantidade());
        oferta.setStatus(StatusOferta.ATIVA);
        oferta.setUltimaModificacao(Instant.now());

        request.itensDesejados().forEach(
                itemDesejadoRequest -> {
                    ItemDesejado itemDesejado = new ItemDesejado();
                    itemDesejado.setCartaCatalogoId(UUID.fromString(itemDesejadoRequest.cartaCatalogoId()));
                    itemDesejado.setOferta(oferta);
                    itemDesejado.setQuantidade(itemDesejadoRequest.quantidade());
                    itemDesejado.setEstadoMinimo(EstadoMinimo.valueOf(itemDesejadoRequest.estadoMinimo()));

                    StringJoiner joinerAcabamento = new StringJoiner(",");
                    itemDesejadoRequest.acabamentosAceitos().forEach(joinerAcabamento::add);
                    itemDesejado.setAcabamentos(joinerAcabamento.toString());

                    StringJoiner joinerPromo = new StringJoiner(",");
                    itemDesejadoRequest.promosAceitas().forEach(joinerPromo::add);
                    itemDesejado.setTiposPromo(joinerPromo.toString());

                    StringJoiner joinerIdioma = new StringJoiner(",");
                    itemDesejadoRequest.idiomasAceitos().forEach(joinerPromo::add);
                    itemDesejado.setIdiomasDesejados(joinerPromo.toString());

                    oferta.getItensDesejados().add(itemDesejado);
                }
        );

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        ItemInventarioDTO itemOferecido = inventarioFacade
                .buscaPorIdEUsuarioId(request.itemInventarioOfertadoId(), usuarioId);

        Set<UUID> setCartaCatalogoId = new HashSet<>();
        setCartaCatalogoId.add(UUID.fromString(itemOferecido.cartaCatalogoId()));
        oferta.getItensDesejados()
                .forEach(itemDesejado -> setCartaCatalogoId.add(itemDesejado.getCartaCatalogoId()));

        Map<UUID, CartaCatalogoDTO> detalhesCartaMap = catalogoFacade.buscaPorConjuntoIdsMap(setCartaCatalogoId);

        List<OfertaResponseDTO.ItemDesejadoResponseDTO> itensDesejadosResponse = new ArrayList<>();
        ofertaSalva.getItensDesejados()
                .forEach(itemDesejado -> {

                    CartaCatalogoDTO detalheItemDesejado = detalhesCartaMap.get(itemDesejado.getCartaCatalogoId());
                    itensDesejadosResponse.add(new OfertaResponseDTO.ItemDesejadoResponseDTO(
                            itemDesejado.getId().toString(),
                            itemDesejado.getCartaCatalogoId().toString(),
                            Arrays.stream(itemDesejado.getAcabamentos().split(",")).toList(),
                            Arrays.stream(itemDesejado.getTiposPromo().split(",")).toList(),
                            Arrays.stream(itemDesejado.getIdiomasDesejados().split(",")).toList(),
                            itemDesejado.getQuantidade(),
                            itemDesejado.getEstadoMinimo().toString(),
                            detalheItemDesejado.nome(),
                            detalheItemDesejado.imagemFrenteUrl(),
                            detalheItemDesejado.siglaEdicao(),
                            detalheItemDesejado.numeroColecionador()
                    ));
                });

        CartaCatalogoDTO detalhesItemOferecido = detalhesCartaMap.get(UUID.fromString(itemOferecido.cartaCatalogoId()));
        return new OfertaResponseDTO(
                ofertaSalva.getId().toString(),
                ofertaSalva.getUsuarioId().toString(),
                ofertaSalva.getStatus(),
                ofertaSalva.getUltimaModificacao(),
                new OfertaResponseDTO.ItemOferecidoResponseDTO(
                        itemOferecido.id(),
                        itemOferecido.cartaCatalogoId(),
                        itemOferecido.idioma(),
                        itemOferecido.estado(),
                        itemOferecido.acabamento(),
                        itemOferecido.promo(),
                        detalhesItemOferecido.nome(),
                        detalhesItemOferecido.imagemFrenteUrl(),
                        detalhesItemOferecido.siglaEdicao(),
                        detalhesItemOferecido.numeroColecionador()
                ),
                itensDesejadosResponse
        );

    }
}
