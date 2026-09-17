package br.com.marceloneuro.mtgtrade.gateway.filter;

import br.com.marceloneuro.mtgtrade.gateway.security.TokenValidator;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final TokenValidator tokenValidator;

    public JwtAuthenticationGatewayFilterFactory(TokenValidator tokenValidator) {
        super(Config.class);
        this.tokenValidator = tokenValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null) {
                return rejeitaRequisicao(exchange);
            }
            if (!token.startsWith("Bearer ")) {
                return rejeitaRequisicao(exchange);
            }


            String usuario;
            try {
                // caso a autenticação falhe ao invés de lançar uma exception o filter deve  rejeitar a requisição
                usuario = tokenValidator.extrairUsuario(token.substring(7));
            } catch (Exception e) {
                return rejeitaRequisicao(exchange);
            }

            ServerHttpRequest requestMutada = exchange.getRequest()
                    .mutate()
                    // caso um usuário mal intencionado coloque uma header X-User-Id ela vai ser excluída com o .set
                    .headers(httpHeaders -> httpHeaders.set("X-User-Id", usuario))
                    .build();

            return chain.filter(exchange.mutate().request(requestMutada).build());
        });
    }

    private Mono<Void> rejeitaRequisicao(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {

    }
}
