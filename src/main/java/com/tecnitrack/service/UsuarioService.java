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

    @Transactional
    public Usuario actualizarDatosContacto(String correoActual,
            String nombre, String correoNuevo, String telefono) {

        // Buscar al usuario autenticado
        Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        // Validar campos obligatorios
        if (nombre == null || nombre.isBlank()
                || correoNuevo == null || correoNuevo.isBlank()
                || telefono == null || telefono.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios");
        }

        // Normalizar los datos
        nombre = nombre.trim();
        correoNuevo = correoNuevo.trim().toLowerCase();
        telefono = telefono.trim();

        // Validar formato del correo
        if (!correoNuevo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido");
        }

        // Verificar que el nuevo correo no pertenezca a otro usuario
        if (!correoNuevo.equals(usuario.getCorreo())
                && usuarioRepository.existsByCorreo(correoNuevo)) {

            throw new IllegalArgumentException(
                    "El correo ya está registrado");
        }

        // Actualizar únicamente los datos de contacto
        usuario.setNombre(nombre);
        usuario.setCorreo(correoNuevo);
        usuario.setTelefono(telefono);

        // Guardar los cambios en MySQL
        return usuarioRepository.save(usuario);
    }
    // HU19 - Registrar cuentas del personal

    @Transactional
    public Usuario registrarPersonal(Usuario usuario, String nombreRol) {

        // Validar campos obligatorios
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || usuario.getTelefono() == null || usuario.getTelefono().isBlank()
                || usuario.getContrasenaHash() == null
                || usuario.getContrasenaHash().isBlank()
                || nombreRol == null || nombreRol.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios");
        }

        // Normalizar los datos
        String nombre = usuario.getNombre().trim();
        String correo = usuario.getCorreo().trim().toLowerCase();
        String telefono = usuario.getTelefono().trim();
        String contrasena = usuario.getContrasenaHash();

        // Validar formato del correo
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido");
        }

        // Valida que el correo no esté registrado
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado");
        }

        // Validar contraseña
        if (contrasena.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres");
        }

        // Permite únicamente roles del personal
        if (!nombreRol.equals("Técnico")
                && !nombreRol.equals("Administrador")) {

            throw new IllegalArgumentException(
                    "El rol seleccionado no es válido");
        }

        // Busca el rol en MySQL
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalStateException(
                "No existe el rol seleccionado"));

        // Asignar los datos
        usuario.setIdUsuario(null);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        usuario.setRol(rol);
        usuario.setActivo(true);

        // Guarda en MySQL
        return usuarioRepository.save(usuario);
    }
    // 

    @Transactional
    public Usuario editarUsuario(Long idUsuario,
            String nombre, String correo, String telefono) {

        // Buscar el usuario por su ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        // Validar campos obligatorios
        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || telefono == null || telefono.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios");
        }

        // Normalizar datos
        nombre = nombre.trim();
        correo = correo.trim().toLowerCase();
        telefono = telefono.trim();

        // Validar correo electrónico
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido");
        }

        // Verificar que el correo no pertenezca a otro usuario
        if (!correo.equals(usuario.getCorreo())
                && usuarioRepository.existsByCorreo(correo)) {

            throw new IllegalArgumentException(
                    "El correo ya está registrado");
        }

        // Actualizar datos
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);

        // Guardar cambios en MySQL
        return usuarioRepository.save(usuario);
    }

    // Cambiar rol de usuario
    @Transactional
    public Usuario cambiarRol(Long idUsuario, String nombreRol) {

        usuarioRepository.bloquearRolAdministrador()
                .orElseThrow(() -> new IllegalStateException(
                "No se pudo bloquear el rol Administrador"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        if (!"Técnico".equals(nombreRol)
                && !"Administrador".equals(nombreRol)) {
            throw new IllegalArgumentException(
                    "El rol seleccionado no es válido");
        }

        // Proteger al último administrador activo
        if ("Administrador".equals(usuario.getRol().getNombre())
                && Boolean.TRUE.equals(usuario.getActivo())
                && !"Administrador".equals(nombreRol)) {

            long administradoresActivos
                    = usuarioRepository.countByRol_NombreAndActivoTrue(
                            "Administrador");

            if (administradoresActivos <= 1) {
                throw new IllegalStateException(
                        "No se puede cambiar el rol del último administrador activo");
            }
        }

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el rol seleccionado"));

        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    //  Activa o desactiva usuarios
    @Transactional
    public Usuario cambiarEstado(Long idUsuario, boolean activo) {

        usuarioRepository.bloquearRolAdministrador()
                .orElseThrow(() -> new IllegalStateException(
                "No se pudo bloquear el rol Administrador"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        // Proteger al último administrador activo
        if ("Administrador".equals(usuario.getRol().getNombre())
                && Boolean.TRUE.equals(usuario.getActivo())
                && !activo) {

            long administradoresActivos
                    = usuarioRepository.countByRol_NombreAndActivoTrue(
                            "Administrador");

            if (administradoresActivos <= 1) {
                throw new IllegalStateException(
                        "No se puede desactivar al último administrador activo");
            }
        }

        usuario.setActivo(activo);

        return usuarioRepository.save(usuario);
    }

    //  Restablecer contraseña de usuario
    @Transactional
    public Usuario restablecerContrasena(
            Long idUsuario, String nuevaContrasena) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        if (nuevaContrasena == null
                || nuevaContrasena.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres");
        }

        usuario.setContrasenaHash(
                passwordEncoder.encode(nuevaContrasena));

        // Invalidar las sesiones anteriores
        usuario.setVersionSesion(usuario.getVersionSesion() + 1L);

        return usuarioRepository.save(usuario);
    }

}
