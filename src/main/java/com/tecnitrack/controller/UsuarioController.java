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

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
}
