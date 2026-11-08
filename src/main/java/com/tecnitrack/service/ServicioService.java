package com.tecnitrack.service;

import com.tecnitrack.domain.Servicio;
import com.tecnitrack.repository.ServicioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    // Obtener todos los servicios para el administrador
    @Transactional(readOnly = true)
    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    // Obtener solamente los servicios activos
    @Transactional(readOnly = true)
    public List<Servicio> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc();
    }

    // Buscar servicio por ID
    @Transactional(readOnly = true)
    public Servicio buscarPorId(Long idServicio) {
        return servicioRepository.findById(idServicio)
                .orElseThrow(()
                        -> new IllegalArgumentException("Servicio no encontrado."));
    }

    // Crear un nuevo servicio
    @Transactional
    public void crearServicio(String nombre, String descripcion) {

        validarCampos(nombre, descripcion);

        String nombreLimpio = nombre.trim();

        if (servicioRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new IllegalArgumentException(
                    "Ya existe un servicio con ese nombre.");
        }

        Servicio servicio = new Servicio();
        servicio.setNombre(nombreLimpio);
        servicio.setDescripcion(descripcion.trim());
        servicio.setActivo(true);

        servicioRepository.save(servicio);
    }

    // Editar un servicio existente
    @Transactional
    public void editarServicio(Long idServicio,
            String nombre,
            String descripcion) {

        validarCampos(nombre, descripcion);

        Servicio servicio = buscarPorId(idServicio);
        String nombreLimpio = nombre.trim();

        servicioRepository.findByNombreIgnoreCase(nombreLimpio)
                .ifPresent(existente -> {
                    if (!existente.getIdServicio().equals(idServicio)) {
                        throw new IllegalArgumentException(
                                "Ya existe un servicio con ese nombre.");
                    }
                });

        servicio.setNombre(nombreLimpio);
        servicio.setDescripcion(descripcion.trim());

        servicioRepository.save(servicio);
    }

    // Activar o desactivar sin eliminar de la base de datos
    @Transactional
    public void cambiarEstado(Long idServicio, boolean activo) {

        Servicio servicio = buscarPorId(idServicio);
        servicio.setActivo(activo);

        servicioRepository.save(servicio);
    }

    // Validar campos obligatorios
    private void validarCampos(String nombre, String descripcion) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del servicio es obligatorio.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción del servicio es obligatoria.");
        }

        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar 100 caracteres.");
        }

        if (descripcion.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar 500 caracteres.");
        }
    }
}
