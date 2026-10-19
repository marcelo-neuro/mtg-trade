package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.ListaImportacaoRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.PreviaImportacaoItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
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

    public List<PreviaImportacaoItemResponseDTO> gerarPreviaImportacao(ListaImportacaoRequestDTO request) {
        Set<String> setEdicoes = new HashSet<>();
        Set<String> setNumerosColecionador = new HashSet<>();

        // Armazena as linhas extraídas com regex para não precisarmos repetir a triagem
        List<LinhaExtraida> linhasExtraidas = new ArrayList<>();

        request.lista().lines() // Itera linha por linha
                .filter(linha -> !linha.isBlank())
                .forEach(linha -> {
                    Matcher matcher = PADRAO_MOXFIELD.matcher(linha.trim());

                    if (matcher.find()) {
                        String edicao =  matcher.group(3);
                        String numeroColecionador =  matcher.group(4);

                        linhasExtraidas.add(new LinhaExtraida(
                                linha,
                                edicao,
                                numeroColecionador,
                                Integer.parseInt(matcher.group(1)), // Quantidade
                                true
                        ));

                        setEdicoes.add(edicao);
                        setNumerosColecionador.add(numeroColecionador);
                    } else {
                        linhasExtraidas.add(new LinhaExtraida(
                                linha,
                                null,
                                null,
                                null,
                                false
                        ));
                    }
                });

    }
}
