/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.tecnitrack.service;

import com.tecnitrack.domain.Solicitud;
import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.SolicitudRepository;
import com.tecnitrack.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudService(
            SolicitudRepository solicitudRepository,
            UsuarioRepository usuarioRepository) {

        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // HU05: Registrar una nueva solicitud
    @Transactional
    public Solicitud registrarSolicitud(
            Long idUsuario,
            String asunto,
            String descripcion) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario no existe"));

        if (asunto == null || asunto.isBlank()) {
            throw new IllegalArgumentException(
                    "El asunto es obligatorio");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripcion es obligatoria");
        }

        if (asunto.length() > 150 ||
                descripcion.length() > 1000) {
            throw new IllegalArgumentException(
                    "El texto supera la longitud permitida");
        }

        Solicitud solicitud = new Solicitud();

        solicitud.setUsuario(usuario);
        solicitud.setAsunto(asunto.trim());
        solicitud.setDescripcion(descripcion.trim());
        solicitud.setEstado("PENDIENTE");

        return solicitudRepository.save(solicitud);
    }

    // HU06: Consultar solicitudes de un usuario
    @Transactional(readOnly = true)
    public List<Solicitud> listarMisSolicitudes(
            Long idUsuario) {

        return solicitudRepository
                .findByUsuario_IdUsuarioOrderByFechaCreacionDesc(
                        idUsuario);
    }
}

