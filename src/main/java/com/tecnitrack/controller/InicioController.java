package com.tecnitrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String mostrarInicio(Authentication authentication, Model model) {

        model.addAttribute("correo", authentication.getName());

        return "inicio";
    }
}
