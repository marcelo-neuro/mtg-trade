package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;

@Service
@RequiredArgsConstructor
class ScryfallClient {

    private final RestClient scryfallRestClient;
    private final ObjectMapper objectMapper;

    public String obtemUrlBulkDataRecente() {
        String respostaJson = scryfallRestClient.get()
                .uri("/bulk-data")
                .retrieve()
                .body(String.class);

        JsonNode raiz = objectMapper.readTree(respostaJson);

        JsonNode data = raiz.get("data");

        for (JsonNode node : data) {

            if (node.path("type").asString().equals("default_cards")) {
                return node.path("jsonl_download_uri").asString();
            }
        }

        throw new RuntimeException("Link de bulk data não disponível.");
    }

    public InputStream downloadJSONLBulkData(String bulkDataUrl) {
        URI bulkDataUri = URI.create(bulkDataUrl);

        return scryfallRestClient.get()
                .uri(bulkDataUri)
                .retrieve()
                .body(InputStream.class);
    }
}
