package br.com.marceloneuro.mtgtrade.iam.internal.web;

import br.com.marceloneuro.mtgtrade.iam.internal.application.service.LoginService;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.LoginRequestDTO;
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
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        String response = loginService.autenticar(loginRequest);

        return ResponseEntity.ok(response);
    }
}
