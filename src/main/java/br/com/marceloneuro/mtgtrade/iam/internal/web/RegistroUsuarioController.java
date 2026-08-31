package br.com.marceloneuro.mtgtrade.iam.internal.web;

import br.com.marceloneuro.mtgtrade.iam.internal.application.service.RegistroService;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.RegistroUsuarioRequestDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.RegistroUsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/registro")
@RequiredArgsConstructor
public class RegistroUsuarioController {

    private final RegistroService registroService;

    @PostMapping
    public ResponseEntity<RegistroUsuarioResponseDTO> registrarUsuario(@RequestBody RegistroUsuarioRequestDTO request) {
        RegistroUsuarioResponseDTO response = registroService.criarUsuario(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{email}")
                .buildAndExpand(response.email())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
