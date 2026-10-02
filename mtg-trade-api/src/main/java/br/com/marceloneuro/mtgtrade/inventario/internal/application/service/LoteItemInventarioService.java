package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation.ValidadorItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoteItemInventarioService {

    private final ItemInventarioRepository itemInventarioRepository;
    private final CatalogoFacade catalogoFacade;

    private final List<ValidadorItemInventario> validadores;
}
