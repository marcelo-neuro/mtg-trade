package br.com.marceloneuro.mtgtrade.catalogo.internal.facade;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.CatalogoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogoFacadeImpl implements CatalogoFacade {

    private final CatalogoRepository catalogoRepository;

    @Override
    public CartaCatalogoDTO obterPorPrintId(String printId) {
        return catalogoRepository.findByPrintId(printId)
                .map(cartaCatalogo -> new CartaCatalogoDTO(
                        cartaCatalogo.getOracleId(),
                        cartaCatalogo.getPrintId(),
                        cartaCatalogo.getNome(),
                        cartaCatalogo.getEdicao(),
                        cartaCatalogo.getImagemFrenteUrl(),
                        cartaCatalogo.getImagemVersoUrl()
                ))
                .orElseThrow(() -> new EntityNotFoundException("Print Id não existe. Print Id: " + printId));
    }
}
