package br.com.marceloneuro.mtgtrade.gateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class TokenValidator {

    @Value("${api.security.token.secret}")
    private String segredoToken;

    @Value("${api.security.token.issuer}")
    private String issuer;

    public String extrairUsuario(String token) {
        Algorithm algoritmo = Algorithm.HMAC256(segredoToken);
        JWTVerifier verificadorJWT = JWT.require(algoritmo)
                .withIssuer(issuer)
                .build();

        DecodedJWT jwtDecodificado = verificadorJWT.verify(token);
        return jwtDecodificado.getSubject();
    }

}
