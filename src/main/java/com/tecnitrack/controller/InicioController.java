package com.tecnitrack.controller;

import com.tecnitrack.service.ServicioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final ServicioService servicioService;

    public InicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // Página pública
    @GetMapping("/")
    public String mostrarPaginaPublica(Model model) {

        model.addAttribute(
                "servicios",
                servicioService.listarActivos()
        );

        return "pagina-publica";
    }

    // Panel privado después de iniciar sesión
    @GetMapping("/inicio")
    public String mostrarInicio(
            Authentication authentication,
            Model model) {

        model.addAttribute("correo", authentication.getName());

        return "inicio";
    }
}
