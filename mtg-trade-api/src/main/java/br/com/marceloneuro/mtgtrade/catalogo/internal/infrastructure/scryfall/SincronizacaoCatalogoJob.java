package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.CatalogoRepository;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallLayoutException;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.exceptions.ScryfallMissingImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
@RequiredArgsConstructor
public class SincronizacaoCatalogoJob {

    private final ScryfallParser scryfallParser;
    private final ScryfallClient scryfallClient;
    private final CatalogoRepository catalogoRepository;

    // Coordena o fluxo de ETL das cartas do Scryfall
    public void sincronizaCatalogo() {
        String urlBulk = scryfallClient.obtemUrlBulkDataRecente();
        try {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new GZIPInputStream(scryfallClient.downloadJSONLBulkData(urlBulk)),
                            StandardCharsets.UTF_8
                    )
            )) {
                List<CartaCatalogo> lista1000CartasCatalogo = new ArrayList<>();

                String linha;
                while ((linha = reader.readLine()) != null) {
                    try {
                        lista1000CartasCatalogo.add(scryfallParser.converteLinhaJsonEmCartaCatalogo(linha));

                        if (lista1000CartasCatalogo.size() == 1000) {
                            catalogoRepository.saveAll(lista1000CartasCatalogo);

                            lista1000CartasCatalogo.clear();
                        }
                    } catch (ScryfallMissingImageException | ScryfallLayoutException scryfallException) {
                        // Carta não pode ser processada
                    }
                }

                // Salva as cartas residuais caso a lista termine com menos de 1000 cartas
                if (!lista1000CartasCatalogo.isEmpty()) {
                    catalogoRepository.saveAll(lista1000CartasCatalogo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar a sincronização com o Scryfall.", e);
        }
    }
}
