package com.tecnitrack.controller;

import com.tecnitrack.domain.Usuario;
import com.tecnitrack.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.tecnitrack.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService,
            UsuarioRepository usuarioRepository) {

        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @InitBinder("usuario")
    public void configurarCamposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields(
                "nombre",
                "correo",
                "telefono",
                "contrasenaHash"
        );
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @ModelAttribute("usuario") Usuario usuario,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        try {

            usuarioService.registrarCliente(usuario, confirmarContrasena);

            return "redirect:/registro?exito";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("error", e.getMessage());

            return "usuario/registro";
        }
    }
    // HU04 - Mostrar datos de contacto

    @GetMapping("/usuario/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {

        String correoActual = authentication.getName();

        Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el usuario"));

        model.addAttribute("usuario", usuario);

        return "usuario/perfil";
    }

// HU04 - Actualizar datos de contacto
    @PostMapping("/usuario/perfil")
    public String actualizarPerfil(
            Authentication authentication,
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Model model) {

        String correoActual = authentication.getName();

        try {

            Usuario usuario = usuarioService.actualizarDatosContacto(
                    correoActual, nombre, correo, telefono);

            // Si cambió el correo, debe iniciar sesión nuevamente
            if (!correoActual.equals(usuario.getCorreo())) {

                new SecurityContextLogoutHandler()
                        .logout(request, response, authentication);

                return "redirect:/login?correoActualizado";
            }

            return "redirect:/usuario/perfil?exito";

        } catch (IllegalArgumentException e) {

            Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                    .orElseThrow(() -> new IllegalStateException(
                    "No se encontró el usuario"));

            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());

            return "usuario/perfil";
        }
    }
}
