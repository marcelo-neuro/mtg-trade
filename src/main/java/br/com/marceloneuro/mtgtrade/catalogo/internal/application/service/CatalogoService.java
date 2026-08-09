package br.com.marceloneuro.mtgtrade.catalogo.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.CatalogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CatalogoRepository catalogoRepository;

    // Busca cartas no banco pelo nome, buscando o paramêtro nome dentro do campo nome (É como utilizar o LIKE '%valor%')
    public Page<CartaCatalogoDTO> buscarCartasPorNome(String nome, Pageable pageable) {
        return catalogoRepository.findByNameContainingIgnoreCase(nome, pageable)
                .map(cartaCatalogo -> new CartaCatalogoDTO(
                        cartaCatalogo.getOracleId(),
                        cartaCatalogo.getPrintId(),
                        cartaCatalogo.getNome(),
                        cartaCatalogo.getEdicao(),
                        cartaCatalogo.getImagemUrl()
                ));
    }
}
