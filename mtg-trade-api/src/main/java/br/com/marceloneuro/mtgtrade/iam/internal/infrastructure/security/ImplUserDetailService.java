package br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.security;

import br.com.marceloneuro.mtgtrade.iam.internal.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImplUserDetailService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new ImplUserDetails(usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("E-mail não existente.")));
    }
}
