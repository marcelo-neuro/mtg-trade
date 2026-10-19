package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TradutorListaService {

    private final CatalogoFacade catalogoFacade;

    // Regex para o formato do Moxfield: <Quantidade> <Nome da carta> (<Sigla do conjunto>) <Número de colecionador>
    // Exemplo: 1 Emry, Lurker of the Loch (EOC) 71
    private static final Pattern PADRAO_MOXFIELD = Pattern.compile("^(\\d+)\\s+(.+?)\\s+\\((.+?)\\)\\s+(\\S+)$");

    private record LinhaExtraida(
            String linhaOriginal,
            String edicao,
            String numeroColecionador,
            Integer quantidade,
            boolean formatoValido
    ) {}


}
