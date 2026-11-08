package com.tecnitrack.controller;

import com.tecnitrack.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServicioPublicoController {

    private final ServicioService servicioService;

    public ServicioPublicoController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping("/servicios")
    public String consultarServicios(Model model) {

        model.addAttribute("servicios",
                servicioService.listarActivos());

        return "servicios";
    }
}
