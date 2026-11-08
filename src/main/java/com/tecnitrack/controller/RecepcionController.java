
package com.tecnitrack.controller;

import com.tecnitrack.domain.OrdenReparacion;
import com.tecnitrack.repository.OrdenReparacionRepository;
import com.tecnitrack.service.OrdenReparacionService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/recepciones")
public class RecepcionController {

    private final OrdenReparacionService ordenService;
    private final OrdenReparacionRepository ordenRepository;

    public RecepcionController(
            OrdenReparacionService ordenService,
            OrdenReparacionRepository ordenRepository) {

        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
    }

    // Mostrar órdenes pendientes de recepción
    @GetMapping
    public String listarPendientes(Model model) {

        model.addAttribute(
                "ordenes",
                ordenRepository.findByEstadoActual("Por recibir")
        );

        return "admin/recepciones";
    }

    // Mostrar formulario de recepción
    @GetMapping("/{id}")
    public String formularioRecepcion(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        OrdenReparacion orden = ordenRepository.findById(id)
                .orElse(null);

        if (orden == null
                || !"Por recibir".equals(orden.getEstadoActual())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La orden no existe o ya fue recibida.");

            return "redirect:/admin/recepciones";
        }

        model.addAttribute("orden", orden);

        return "admin/formulario-recepcion";
    }

    // Registrar recepción del equipo
    @PostMapping("/{id}/guardar")
    public String guardarRecepcion(
            @PathVariable Long id,
            @RequestParam(defaultValue = "")
            String condicionRecepcion,
            @RequestParam(defaultValue = "")
            String accesorios,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {

            ordenService.registrarRecepcion(
                    id,
                    principal.getName(),
                    condicionRecepcion,
                    accesorios
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "El equipo fue recibido correctamente.");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/admin/recepciones";
    }
}
