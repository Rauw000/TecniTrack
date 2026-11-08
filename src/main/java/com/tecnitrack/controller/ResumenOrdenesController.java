package com.tecnitrack.controller;

import com.tecnitrack.repository.OrdenReparacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/ordenes")
public class ResumenOrdenesController {

    private final OrdenReparacionRepository ordenRepository;

    public ResumenOrdenesController(
            OrdenReparacionRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @GetMapping("/resumen")
    public String mostrarResumen(
            @RequestParam(required = false) String estado,
            Model model) {

        String[] estados = {
            "Por recibir",
            "Recibido",
            "En diagnóstico",
            "Pendiente de aprobación",
            "Reparación autorizada",
            "En reparación",
            "Listo para entregar",
            "Pendiente de devolución"
        };

        java.util.Map<String, Long> conteos
                = new java.util.LinkedHashMap<>();

        for (String estadoActual : estados) {
            conteos.put(
                    estadoActual,
                    ordenRepository.countByEstadoActual(estadoActual)
            );
        }

        model.addAttribute("conteos", conteos);

        if (estado != null && conteos.containsKey(estado)) {
            model.addAttribute(
                    "ordenes",
                    ordenRepository.findByEstadoActual(estado)
            );
            model.addAttribute("estadoSeleccionado", estado);
        }

        return "admin/resumen-ordenes";
    }
}
