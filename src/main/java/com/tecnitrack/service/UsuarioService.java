package com.tecnitrack.service;

import com.tecnitrack.domain.Rol;
import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.RolRepository;
import com.tecnitrack.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarCliente(Usuario usuario, String confirmarContrasena) {

        // Validar que los datos obligatorios estén completos
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || usuario.getTelefono() == null || usuario.getTelefono().isBlank()
                || usuario.getContrasenaHash() == null
                || usuario.getContrasenaHash().isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios");
        }

        // Validar el formato del correo
        String correo = usuario.getCorreo().trim().toLowerCase();

        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido");
        }

        // Comprobar que el correo no esté registrado
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado");
        }

        // Validar la contraseña
        String contrasena = usuario.getContrasenaHash();

        if (contrasena.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres");
        }

        // Confirmar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden");
        }

        // Buscar el rol Cliente
        Rol rolCliente = rolRepository.findByNombre("Cliente")
                .orElseThrow(() -> new IllegalStateException(
                "No existe el rol Cliente"));

        // Proteger la contraseña antes de guardarla
        usuario.setContrasenaHash(passwordEncoder.encode(contrasena));

        // Asignar los datos del usuario
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(usuario.getTelefono().trim());
        usuario.setRol(rolCliente);
        usuario.setActivo(true);
        usuario.setIdUsuario(null);

        // Guardar el usuario en MySQL
        return usuarioRepository.save(usuario);
    }
}
