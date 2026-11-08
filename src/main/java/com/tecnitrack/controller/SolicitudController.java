package com.tecnitrack.controller;

import com.tecnitrack.domain.OrdenReparacion;
import com.tecnitrack.service.OrdenReparacionService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/cliente/solicitudes")
public class SolicitudController {

    private final OrdenReparacionService ordenService;

    public SolicitudController(
            OrdenReparacionService ordenService) {
        this.ordenService = ordenService;
    }

    // Mostrar formulario para registrar una solicitud
    @GetMapping("/nueva")
    public String nuevaSolicitud() {
        return "cliente/nueva-solicitud";
    }

    // Guardar solicitud de reparación
    @PostMapping("/guardar")
    public String guardarSolicitud(
            @RequestParam(defaultValue = "") String tipo,
            @RequestParam(defaultValue = "") String marca,
            @RequestParam(defaultValue = "") String modelo,
            @RequestParam(defaultValue = "") String numeroSerie,
            @RequestParam(defaultValue = "") String fallaReportada,
            Principal principal,
            Model model) {

        try {
            OrdenReparacion orden = ordenService.registrarSolicitud(
                    principal.getName(),
                    tipo,
                    marca,
                    modelo,
                    numeroSerie,
                    fallaReportada
            );

            model.addAttribute("numeroOrden",
                    orden.getNumeroOrden());

            return "cliente/solicitud-exitosa";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            // Conservar los datos si ocurre un error
            model.addAttribute("tipo", tipo);
            model.addAttribute("marca", marca);
            model.addAttribute("modelo", modelo);
            model.addAttribute("numeroSerie", numeroSerie);
            model.addAttribute("fallaReportada", fallaReportada);

            return "cliente/nueva-solicitud";
        }
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(
            Principal principal,
            Model model) {

        model.addAttribute(
                "solicitudes",
                ordenService.listarSolicitudesCliente(
                        principal.getName())
        );

        return "cliente/mis-solicitudes";
    }

    @GetMapping("/detalle/{id}")
    public String detalleSolicitud(
            @PathVariable Long id,
            Principal principal,
            Model model) {

        try {
            OrdenReparacion orden
                    = ordenService.obtenerDetalleCliente(
                            id, principal.getName());

            model.addAttribute("orden", orden);

            return "cliente/detalle-solicitud";

        } catch (IllegalArgumentException e) {

            return "redirect:/cliente/solicitudes/mis-solicitudes";
        }
    }

}
