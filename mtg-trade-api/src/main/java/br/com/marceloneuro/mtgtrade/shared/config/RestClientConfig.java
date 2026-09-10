package br.com.marceloneuro.mtgtrade.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient ScryfallRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.scryfall.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mtg-Trade-projeto/0.1.0")
                .build();
    }
    
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
