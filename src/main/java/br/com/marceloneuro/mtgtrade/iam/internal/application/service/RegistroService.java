package br.com.marceloneuro.mtgtrade.iam.internal.application.service;

import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.RegistroUsuarioRequestDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto.RegistroUsuarioResponseDTO;
import br.com.marceloneuro.mtgtrade.iam.internal.domain.Usuario;
import br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegistroUsuarioResponseDTO criarUsuario(RegistroUsuarioRequestDTO request) {
        Usuario novoUsuario = new Usuario();

        novoUsuario.setNomeUsuario(request.nomeUsuario());
        novoUsuario.setEmail(request.email());
        novoUsuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario usuarioCriado = usuarioRepository.save(novoUsuario);

        return new RegistroUsuarioResponseDTO(usuarioCriado.getNomeUsuario(), usuarioCriado.getEmail());
    }
}
