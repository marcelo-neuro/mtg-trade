package br.com.marceloneuro.mtgtrade.iam.internal.application.service;

import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.LoginRequestDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.TokenDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.security.ImplUserDetails;
import br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public TokenDTO autenticar(LoginRequestDTO loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());
        Authentication autenticacao = authenticationManager.authenticate(authToken);

        ImplUserDetails usuario = (ImplUserDetails) autenticacao.getPrincipal();

        return new TokenDTO(tokenService.gerarToken(usuario));
    }
}
