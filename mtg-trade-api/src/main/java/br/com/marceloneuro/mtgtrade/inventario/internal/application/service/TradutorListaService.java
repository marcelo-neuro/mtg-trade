package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.ChaveBuscaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.ListaImportacaoRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.PreviaImportacaoItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
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

    // Passa o Regex pela lista e extrai todas as linhas para o DTO interno.
    // Após isso puxa o map de ChaveBusca e CartaCatalogo do CatalogoFacade.
    // Então iteramos pelas listas de linhas extraídas e organizamos a resposta.
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

        Map<ChaveBuscaCatalogoDTO, CartaCatalogoDTO> mapDetalhesCarta = catalogoFacade
                .buscarPorConjuntoEdicaoENumeroMap(setEdicoes, setNumerosColecionador);

        return linhasExtraidas.stream()
                .map(linha -> {

                    if (!linha.formatoValido) {
                        return new PreviaImportacaoItemResponseDTO(
                                null,
                                linha.linhaOriginal,
                                null,
                                null,
                                null,
                                null,
                                List.of(),
                                List.of(),
                                false
                        );
                    }

                    ChaveBuscaCatalogoDTO chaveCatalogo =
                            new ChaveBuscaCatalogoDTO(linha.edicao(), linha.numeroColecionador());

                    CartaCatalogoDTO detalhesCarta = mapDetalhesCarta.get(chaveCatalogo);

                    if (detalhesCarta != null) {
                        List<String> acabamentos = detalhesCarta.tiposPromo() != null
                                ? Arrays.stream(detalhesCarta.acabamentos().split(",")).toList()
                                : List.of();
                        List<String> promos = detalhesCarta.tiposPromo() != null
                                ? Arrays.stream(detalhesCarta.tiposPromo().split(",")).toList()
                                : List.of();

                        return new PreviaImportacaoItemResponseDTO(
                                detalhesCarta.id(),
                                detalhesCarta.nome(),
                                detalhesCarta.edicao(),
                                detalhesCarta.numeroColecionador(),
                                linha.quantidade(),
                                detalhesCarta.imagemFrenteUrl(),
                                acabamentos,
                                promos,
                                true
                        );
                    } else {
                        return new PreviaImportacaoItemResponseDTO(
                                null,
                                linha.linhaOriginal,
                                null,
                                null,
                                null,
                                null,
                                List.of(),
                                List.of(),
                                false
                        );
                    }
                }
        ).toList();
    }
}
