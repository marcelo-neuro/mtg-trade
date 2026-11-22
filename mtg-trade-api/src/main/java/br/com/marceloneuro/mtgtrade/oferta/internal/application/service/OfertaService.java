package br.com.marceloneuro.mtgtrade.oferta.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.InventarioFacade;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.ItemInventarioDTO;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.oferta.api.dto.CriaOfertaRequestDTO;
import br.com.marceloneuro.mtgtrade.oferta.api.dto.OfertaResponseDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.*;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.exception.DetalhesItemDesejadoNaoEncontradoException;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.exception.OfertaNaoEncontradaException;
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

        ItemInventarioDTO itemReservado = inventarioFacade.reservarItem(pedidoReserva, usuarioId);

        ItemOferecido itemOferecido = new ItemOferecido();
        itemOferecido.setItemOferecidoId(UUID.fromString(itemReservado.id()));
        itemOferecido.setCartaCatalogoId(UUID.fromString(itemReservado.cartaCatalogoId()));
        itemOferecido.setAcabamento(itemReservado.acabamento());
        itemOferecido.setPromo(itemReservado.promo());
        itemOferecido.setEstado(itemReservado.estado());
        itemOferecido.setIdioma(itemReservado.idioma());
        itemOferecido.setQuantidade(request.quantidade());

        Oferta oferta = new Oferta();
        oferta.setUsuarioId(UUID.fromString(usuarioId));
        oferta.setItemOferecido(itemOferecido);
        oferta.setStatus(StatusOferta.ATIVA);
        oferta.setUltimaModificacao(Instant.now());

        mapeiaListaItensDesejados(oferta, request.itensDesejados());

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        return montaOfertaResponseDTO(ofertaSalva);
    }

    // Não precisa do ID do usuário, pois as ofertas são públicas
    @Transactional(readOnly = true)
    public OfertaResponseDTO buscarPorId(String ofertaId) {
        Oferta oferta = ofertaRepository.findById(UUID.fromString(ofertaId))
                .orElseThrow(() -> new OfertaNaoEncontradaException("Oferta não encontrada."));

        return montaOfertaResponseDTO(oferta);
    }

    // Retiramos as cartas oferecidas da reserva e modificamos o status de uma oferta para CANCELADA
    @Transactional
    public OfertaResponseDTO cancelarOferta(String ofertaId, String usuarioId) {
        Oferta oferta = ofertaRepository
                .findByIdAndUsuarioIdAndStatus(UUID.fromString(ofertaId), UUID.fromString(usuarioId), StatusOferta.ATIVA)
                .orElseThrow(() -> new OfertaNaoEncontradaException("Oferta ativa não encontrada na conta do usuário."));

        inventarioFacade
                .liberarItem(new PedidoReservaDTO(
                        oferta.getItemOferecido().getItemOferecidoId().toString(),
                        oferta.getItemOferecido().getQuantidade()
                ), usuarioId);

        oferta.setStatus(StatusOferta.CANCELADA);
        oferta.setUltimaModificacao(Instant.now());

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        return montaOfertaResponseDTO(ofertaSalva);
    }

    private OfertaResponseDTO mapeiaOfertaResponseDTO(Oferta entidade,
                                                      CartaCatalogoDTO detalhesItemOferecido,
                                                      List<OfertaResponseDTO.ItemDesejadoResponseDTO> itensDesejadosResponse) {
        return new OfertaResponseDTO(
                entidade.getId().toString(),
                entidade.getUsuarioId().toString(),
                entidade.getStatus(),
                entidade.getUltimaModificacao(),
                new OfertaResponseDTO.ItemOferecidoResponseDTO(
                        entidade.getItemOferecido().getItemOferecidoId().toString(),
                        entidade.getItemOferecido().getCartaCatalogoId().toString(),
                        entidade.getItemOferecido().getIdioma(),
                        entidade.getItemOferecido().getEstado(),
                        entidade.getItemOferecido().getAcabamento(),
                        entidade.getItemOferecido().getPromo(),
                        detalhesItemOferecido.nome(),
                        detalhesItemOferecido.imagemFrenteUrl(),
                        detalhesItemOferecido.siglaEdicao(),
                        detalhesItemOferecido.numeroColecionador()
                ),
                itensDesejadosResponse
        );
    }

    private List<OfertaResponseDTO.ItemDesejadoResponseDTO> mapeiaListaItensDesejadosResponse(Oferta entidade,
                                                                                              Map<UUID, CartaCatalogoDTO> detalhesCartaMap) {
        List<OfertaResponseDTO.ItemDesejadoResponseDTO> itensDesejadosResponse = new ArrayList<>();
        entidade.getItensDesejados()
                .forEach(itemDesejado -> {
                    if (!detalhesCartaMap.containsKey(itemDesejado.getCartaCatalogoId())) {
                        throw new DetalhesItemDesejadoNaoEncontradoException(
                                "Detalhe do item desejado não encontrado. ID: " + itemDesejado.getCartaCatalogoId()
                        );
                    }

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

        return itensDesejadosResponse;
    }

    private Set<UUID> mapeiaSetCartaCatalogoUuid(Oferta entidade) {
        Set<UUID> setCartaCatalogoId = new HashSet<>();
        entidade.getItensDesejados()
                .forEach(itemDesejado -> setCartaCatalogoId.add(itemDesejado.getCartaCatalogoId()));

        return setCartaCatalogoId;
    }

    private void mapeiaListaItensDesejados(Oferta entidade,
                                           List<CriaOfertaRequestDTO.ItemDesejadoRequestDTO> itensDesejadosDTO) {
        itensDesejadosDTO.forEach(
                itemDesejadoRequest -> {
                    ItemDesejado itemDesejado = new ItemDesejado();
                    itemDesejado.setCartaCatalogoId(UUID.fromString(itemDesejadoRequest.cartaCatalogoId()));
                    itemDesejado.setOferta(entidade);
                    itemDesejado.setQuantidade(itemDesejadoRequest.quantidade());
                    itemDesejado.setEstadoMinimo(EstadoMinimo.valueOf(itemDesejadoRequest.estadoMinimo()));

                    if (itemDesejadoRequest.acabamentosAceitos() == null) {
                        itemDesejado.setAcabamentos("");
                    } else {
                        StringJoiner joinerAcabamento = new StringJoiner(",");
                        itemDesejadoRequest.acabamentosAceitos().forEach(joinerAcabamento::add);
                        itemDesejado.setAcabamentos(joinerAcabamento.toString());
                    }

                    if (itemDesejadoRequest.promosAceitas() == null) {
                        itemDesejado.setTiposPromo("");
                    } else {
                        StringJoiner joinerPromo = new StringJoiner(",");
                        itemDesejadoRequest.promosAceitas().forEach(joinerPromo::add);
                        itemDesejado.setTiposPromo(joinerPromo.toString());
                    }

                    if (itemDesejadoRequest.idiomasAceitos() == null) {
                        itemDesejado.setIdiomasDesejados("");
                    } else {
                        StringJoiner joinerIdioma = new StringJoiner(",");
                        itemDesejadoRequest.idiomasAceitos().forEach(joinerIdioma::add);
                        itemDesejado.setIdiomasDesejados(joinerIdioma.toString());
                    }

                    entidade.getItensDesejados().add(itemDesejado);
                }
        );
    }

    private OfertaResponseDTO montaOfertaResponseDTO(Oferta entidade) {
        ItemOferecido itemOferecido = entidade.getItemOferecido();

        Set<UUID> setCartaCatalogoId = mapeiaSetCartaCatalogoUuid(entidade);
        setCartaCatalogoId.add(itemOferecido.getItemOferecidoId());

        Map<UUID, CartaCatalogoDTO> detalhesCartaMap = catalogoFacade.buscaPorConjuntoIdsMap(setCartaCatalogoId);

        List<OfertaResponseDTO.ItemDesejadoResponseDTO> itensDesejadosResponse =
                mapeiaListaItensDesejadosResponse(entidade, detalhesCartaMap);

        CartaCatalogoDTO detalhesItemOferecido = detalhesCartaMap.get(UUID.fromString(itemOferecido.getCartaCatalogoId().toString()));
        return mapeiaOfertaResponseDTO(entidade, detalhesItemOferecido, itensDesejadosResponse);
    }
}
