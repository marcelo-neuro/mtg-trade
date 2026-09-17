package br.com.marceloneuro.mtgtrade.iam.internal.web;

import br.com.marceloneuro.mtgtrade.iam.internal.application.service.LoginService;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.LoginRequestDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.TokenDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        TokenDTO response = loginService.autenticar(loginRequest);

        return ResponseEntity.ok(response);
    }
}
