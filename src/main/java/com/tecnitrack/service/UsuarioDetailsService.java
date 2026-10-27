package com.tecnitrack.service;

import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                "Credenciales incorrectas"));

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasenaHash())
                .roles(usuario.getRol().getNombre()
                        .replace("Técnico", "Tecnico"))
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
