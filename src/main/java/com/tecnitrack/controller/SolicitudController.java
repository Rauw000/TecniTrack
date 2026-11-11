/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.tecnitrack.controller;

import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.UsuarioRepository;
import com.tecnitrack.service.SolicitudService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;

    public SolicitudController(
            SolicitudService solicitudService,
            UsuarioRepository usuarioRepository) {

        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener el usuario que inicio sesion
    private Usuario obtenerUsuarioActual(Principal principal) {

        return usuarioRepository
                .findByCorreo(principal.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario autenticado no encontrado"));
    }

    // HU05: Mostrar formulario de nueva solicitud
    @GetMapping("/cliente/solicitudes/nueva")
    public String mostrarFormulario() {

        return "solicitudes/nueva";
    }

    // HU05: Guardar una solicitud
    @PostMapping("/cliente/solicitudes/guardar")
    public String guardarSolicitud(
            @RequestParam String asunto,
            @RequestParam String descripcion,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuarioActual(principal);

        try {

            solicitudService.registrarSolicitud(
                    usuario.getIdUsuario(),
                    asunto,
                    descripcion
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Solicitud registrada correctamente");

            return "redirect:/cliente/solicitudes";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "asuntoAnterior", asunto);

            redirectAttributes.addFlashAttribute(
                    "descripcionAnterior", descripcion);

            return "redirect:/cliente/solicitudes/nueva";
        }
    }

    // HU06: Consultar mis solicitudes
    @GetMapping("/cliente/solicitudes")
    public String listarSolicitudes(
            Principal principal,
            Model model) {

        Usuario usuario = obtenerUsuarioActual(principal);

        model.addAttribute(
                "solicitudes",
                solicitudService.listarMisSolicitudes(
                        usuario.getIdUsuario())
        );

        return "solicitudes/listado";
    }
}

