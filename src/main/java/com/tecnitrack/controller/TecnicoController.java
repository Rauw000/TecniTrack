package com.tecnitrack.controller;

import com.tecnitrack.domain.OrdenReparacion;
import com.tecnitrack.service.DiagnosticoService;
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
@RequestMapping("/tecnico/ordenes")
public class TecnicoController {
    private final DiagnosticoService diagnosticoService;

    public TecnicoController(DiagnosticoService diagnosticoService) {
        this.diagnosticoService = diagnosticoService;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        model.addAttribute("ordenes", diagnosticoService.consultarAsignadas(principal.getName()));
        return "tecnico/ordenes-asignadas";
    }

    @GetMapping("/{id}/diagnostico")
    public String formulario(@PathVariable Long id, Principal principal, Model model,
            RedirectAttributes mensajes) {
        try {
            OrdenReparacion orden = diagnosticoService.consultarOrden(principal.getName(), id);
            model.addAttribute("orden", orden);
            model.addAttribute("diagnostico", diagnosticoService.consultarDiagnostico(principal.getName(), id).orElse(null));
            return "tecnico/registrar-diagnostico";
        } catch (IllegalArgumentException e) {
            mensajes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tecnico/ordenes";
        }
    }

    @PostMapping("/{id}/diagnostico")
    public String guardar(@PathVariable Long id,
            @RequestParam(defaultValue = "") String problemaIdentificado,
            @RequestParam(defaultValue = "") String diagnosticoTecnico,
            @RequestParam(defaultValue = "") String reparacionRecomendada,
            Principal principal, RedirectAttributes mensajes) {
        try {
            diagnosticoService.registrar(principal.getName(), id,
                problemaIdentificado, diagnosticoTecnico, reparacionRecomendada);
            mensajes.addFlashAttribute("exito", "Diagnóstico registrado correctamente.");
            return "redirect:/tecnico/ordenes";
        } catch (IllegalArgumentException e) {
            mensajes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tecnico/ordenes/" + id + "/diagnostico";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            mensajes.addFlashAttribute("error", "Ya existe un diagnóstico para esta orden.");
            return "redirect:/tecnico/ordenes/" + id + "/diagnostico";
        }
    }
}
