package com.tecnitrack;

import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.tecnitrack.domain.UsuarioPrincipal;

@Component
public class UsuarioActivoFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActivoFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UsuarioPrincipal) {

            String correo = authentication.getName();

            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElse(null);

            if (usuario == null
                    || !Boolean.TRUE.equals(usuario.getActivo())) {

                new SecurityContextLogoutHandler().logout(
                        request, response, authentication);

                response.sendRedirect(
                        request.getContextPath() + "/login?inactivo");

                return;
            }
            //Detectar cambios de rol durante una sesión activa
            String rolActual = usuario.getRol().getNombre()
                    .replace("Técnico", "Tecnico");

            String autoridadActual = "ROLE_" + rolActual;

            boolean rolCoincide = authentication.getAuthorities()
                    .stream()
                    .anyMatch(autoridad
                            -> autoridad.getAuthority().equals(autoridadActual));

            if (!rolCoincide) {

                new SecurityContextLogoutHandler().logout(
                        request, response, authentication);

                response.sendRedirect(
                        request.getContextPath() + "/login?rolActualizado");

                return;
            }
            // Detectar sesiones anteriores al cambio de contraseña
            UsuarioPrincipal usuarioPrincipal
                    = (UsuarioPrincipal) authentication.getPrincipal();

            boolean versionCoincide
                    = java.util.Objects.equals(
                            usuario.getVersionSesion(),
                            usuarioPrincipal.getVersionSesion());

            if (!versionCoincide) {

                new SecurityContextLogoutHandler().logout(
                        request, response, authentication);

                response.sendRedirect(
                        request.getContextPath() + "/login?contrasenaActualizada");

                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
