package com.tecnitrack.controller;

import com.tecnitrack.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // Mostrar todos los servicios al administrador
    @GetMapping
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.listarTodos());
        return "admin/servicios";
    }

    // Crear un nuevo servicio
    @PostMapping("/crear")
    public String crearServicio(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            RedirectAttributes redirectAttributes) {

        try {
            servicioService.crearServicio(nombre, descripcion);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Servicio creado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/servicios";
    }

    // Editar un servicio
    @PostMapping("/editar/{id}")
    public String editarServicio(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            RedirectAttributes redirectAttributes) {

        try {
            servicioService.editarServicio(id, nombre, descripcion);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Servicio actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/servicios";
    }

    // Activar o desactivar un servicio
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo,
            RedirectAttributes redirectAttributes) {

        try {
            servicioService.cambiarEstado(id, activo);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Estado del servicio actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/servicios";
    }
}
