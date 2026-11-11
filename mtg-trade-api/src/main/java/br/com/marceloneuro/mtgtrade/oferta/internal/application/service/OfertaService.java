package br.com.marceloneuro.mtgtrade.oferta.internal.application.service;

import br.com.marceloneuro.mtgtrade.inventario.api.InventarioFacade;
import br.com.marceloneuro.mtgtrade.inventario.api.dto.PedidoReservaDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto.CriaOfertaRequestDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto.OfertaResponseDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.Oferta;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.StatusOferta;
import br.com.marceloneuro.mtgtrade.oferta.internal.infrastructure.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final InventarioFacade inventarioFacade;

    @Transactional
    private OfertaResponseDTO criaOferta(CriaOfertaRequestDTO request, String usuarioId) {

        PedidoReservaDTO pedidoReserva = new PedidoReservaDTO(request.itemInventarioOfertadoId(),
                request.quantidade());

        inventarioFacade.reservarItem(pedidoReserva, usuarioId);

        Oferta oferta = new Oferta();
        oferta.setUsuarioId(UUID.fromString(usuarioId));
        oferta.setItemOferecido(UUID.fromString(request.itemInventarioOfertadoId()));
        oferta.setQuantidade(request.quantidade());
        oferta.setStatus(StatusOferta.ATIVA);


    }
}
