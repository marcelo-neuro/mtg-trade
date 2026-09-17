package br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String segredoToken;

    @Value("${api.security.token.issuer}")
    private String issuer;

    public String gerarToken(ImplUserDetails userDetails) {
        Algorithm algoritmo = Algorithm.HMAC256(segredoToken);
        Instant validadeToken = Instant.now().plus(1, ChronoUnit.HOURS);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userDetails.getUsuario().getId().toString())
                .withExpiresAt(validadeToken)
                .sign(algoritmo);
    }
}
