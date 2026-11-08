package com.tecnitrack.controller;

import com.tecnitrack.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import com.tecnitrack.domain.Usuario;
import com.tecnitrack.service.UsuarioService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('Administrador')")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioRepository usuarioRepository,
            UsuarioService usuarioService) {

        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    // Muestra usuarios registrados
    @GetMapping
    public String listarUsuarios(Model model) {

        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "admin/usuarios";
    }
    // Crea las cuentas del personal

    @PostMapping("/crear")
    public String crearPersonal(
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("rol") String rol,
            Model model) {

        try {

            Usuario usuario = new Usuario();

            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setContrasenaHash(contrasena);

            usuarioService.registrarPersonal(usuario, rol);

            return "redirect:/admin/usuarios?creado";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("error", e.getMessage());

            return "admin/usuarios";
        }
    }
    // Edita los datos de los usuarios

    @PostMapping("/editar/{id}")
    public String editarUsuario(
            @PathVariable("id") Long idUsuario,
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            Model model) {

        try {

            usuarioService.editarUsuario(
                    idUsuario, nombre, correo, telefono);

            return "redirect:/admin/usuarios?actualizado";

        } catch (IllegalArgumentException e) {

            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("error", e.getMessage());

            return "admin/usuarios";
        }
    }

    // Cambia el rol del usuario
    @PostMapping("/cambiar-rol/{id}")
    public String cambiarRol(
            @PathVariable("id") Long idUsuario,
            @RequestParam("rol") String nombreRol,
            Model model) {

        try {

            usuarioService.cambiarRol(idUsuario, nombreRol);

            return "redirect:/admin/usuarios?rolActualizado";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("error", e.getMessage());

            return "admin/usuarios";
        }
    }
    // Activar o desactivar usuarios

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(
            @PathVariable("id") Long idUsuario,
            @RequestParam("activo") boolean activo,
            Model model) {

        try {

            usuarioService.cambiarEstado(idUsuario, activo);

            return "redirect:/admin/usuarios?estadoActualizado";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("error", e.getMessage());

            return "admin/usuarios";
        }
    }
    
    // Restablecer contraseña de usuario
@PostMapping("/restablecer-contrasena/{id}")
public String restablecerContrasena(
        @PathVariable("id") Long idUsuario,
        @RequestParam("nuevaContrasena") String nuevaContrasena,
        Model model) {

    try {

        usuarioService.restablecerContrasena(
                idUsuario, nuevaContrasena);

        return "redirect:/admin/usuarios?contrasenaActualizada";

    } catch (IllegalArgumentException | IllegalStateException e) {

        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("error", e.getMessage());

        return "admin/usuarios";
    }
}
}
