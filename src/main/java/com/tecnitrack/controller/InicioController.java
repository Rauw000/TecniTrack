package com.tecnitrack.controller;

import com.tecnitrack.service.ServicioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.tecnitrack.repository.UsuarioRepository;
import com.tecnitrack.repository.OrdenReparacionRepository;

@Controller
public class InicioController {

    private final ServicioService servicioService;
    private final UsuarioRepository usuarioRepository;
    private final OrdenReparacionRepository ordenReparacionRepository;

    public InicioController(
            ServicioService servicioService,
            UsuarioRepository usuarioRepository,
            OrdenReparacionRepository ordenReparacionRepository) {

        this.servicioService = servicioService;
        this.usuarioRepository = usuarioRepository;
        this.ordenReparacionRepository = ordenReparacionRepository;
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

        // Estadísticas exclusivas del administrador
        boolean esAdministrador = authentication.getAuthorities()
                .stream()
                .anyMatch(autoridad
                        -> autoridad.getAuthority().equals("ROLE_Administrador"));

        if (esAdministrador) {

            model.addAttribute("usuariosActivos",
                    usuarioRepository.countByActivoTrue());

            model.addAttribute("totalOrdenes",
                    ordenReparacionRepository.count());

            model.addAttribute("ordenesPorRecibir",
                    ordenReparacionRepository
                            .countByEstadoActual("Por recibir"));

            model.addAttribute("ordenesRecibidas",
                    ordenReparacionRepository
                            .countByEstadoActual("Recibido"));

            model.addAttribute("ordenesRecientes",
                    ordenReparacionRepository
                            .findTop5ByOrderByFechaSolicitudDesc());
            model.addAttribute("listaUsuariosActivos",
                    usuarioRepository.findByActivoTrue());

            // Todas las órdenes
            model.addAttribute("listaTodasOrdenes",
                    ordenReparacionRepository
                            .findAllByOrderByFechaSolicitudDesc());

            // Órdenes por recibir
            model.addAttribute("listaOrdenesPorRecibir",
                    ordenReparacionRepository
                            .findByEstadoActualOrderByFechaSolicitudDesc(
                                    "Por recibir"));

            // Órdenes recibidas
            model.addAttribute("listaOrdenesRecibidas",
                    ordenReparacionRepository
                            .findByEstadoActualOrderByFechaSolicitudDesc(
                                    "Recibido"));

        }

        return "inicio";
    }
}
