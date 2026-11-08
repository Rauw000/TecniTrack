package com.tecnitrack.service;

import com.tecnitrack.domain.Equipo;
import com.tecnitrack.domain.OrdenReparacion;
import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.EquipoRepository;
import com.tecnitrack.repository.OrdenReparacionRepository;
import com.tecnitrack.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenReparacionService {

    private final EquipoRepository equipoRepository;
    private final OrdenReparacionRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdenReparacionService(
            EquipoRepository equipoRepository,
            OrdenReparacionRepository ordenRepository,
            UsuarioRepository usuarioRepository) {

        this.equipoRepository = equipoRepository;
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public OrdenReparacion registrarSolicitud(
            String correoCliente,
            String tipo,
            String marca,
            String modelo,
            String numeroSerie,
            String fallaReportada) {

        // Validar datos obligatorios
        validarCampo(tipo, "El tipo de equipo");
        validarCampo(marca, "La marca");
        validarCampo(modelo, "El modelo");
        validarCampo(fallaReportada, "La descripción de la falla");

        // Validar longitudes
        validarLongitud(tipo, 100, "El tipo de equipo");
        validarLongitud(marca, 100, "La marca");
        validarLongitud(modelo, 100, "El modelo");
        validarLongitud(numeroSerie, 100, "El número de serie");
        validarLongitud(fallaReportada, 2000, "La descripción de la falla");

        // Buscar al cliente autenticado
        Usuario cliente = usuarioRepository.findByCorreo(correoCliente)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "No se encontró la cuenta del cliente."));

        if (!Boolean.TRUE.equals(cliente.getActivo())
                || cliente.getRol() == null
                || !"Cliente".equals(cliente.getRol().getNombre())) {

            throw new IllegalArgumentException(
                    "Solo un cliente activo puede registrar solicitudes.");
        }

        // Crear y guardar el equipo
        Equipo equipo = new Equipo();
        equipo.setCliente(cliente);
        equipo.setTipo(tipo.trim());
        equipo.setMarca(marca.trim());
        equipo.setModelo(modelo.trim());
        equipo.setNumeroSerie(
                numeroSerie == null || numeroSerie.isBlank()
                ? null : numeroSerie.trim());

        equipoRepository.save(equipo);

        // Crear la orden de reparación
        OrdenReparacion orden = new OrdenReparacion();
        orden.setEquipo(equipo);
        orden.setFallaReportada(fallaReportada.trim());
        orden.setFechaSolicitud(LocalDateTime.now());
        orden.setEstadoActual("Por recibir");

        // Generar número único de orden
        String numeroOrden;

        do {
            numeroOrden = "TEC-"
                    + UUID.randomUUID().toString()
                            .substring(0, 12).toUpperCase();
        } while (ordenRepository.existsByNumeroOrden(numeroOrden));

        orden.setNumeroOrden(numeroOrden);

        return ordenRepository.save(orden);
    }

    private void validarCampo(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio.");
        }
    }

    private void validarLongitud(
            String valor, int maximo, String campo) {

        if (valor != null && valor.trim().length() > maximo) {
            throw new IllegalArgumentException(
                    campo + " no puede superar "
                    + maximo + " caracteres.");
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<OrdenReparacion> listarSolicitudesCliente(
            String correoCliente) {

        Usuario cliente = usuarioRepository.findByCorreo(correoCliente)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "No se encontró la cuenta del cliente."));

        if (!Boolean.TRUE.equals(cliente.getActivo())
                || cliente.getRol() == null
                || !"Cliente".equals(cliente.getRol().getNombre())) {

            throw new IllegalArgumentException(
                    "El usuario no tiene permisos de cliente.");
        }

        return ordenRepository
                .findByEquipo_Cliente_IdUsuarioOrderByFechaSolicitudDesc(
                        cliente.getIdUsuario());
    }

    @Transactional(readOnly = true)
    public OrdenReparacion obtenerDetalleCliente(
            Long idOrden, String correoCliente) {

        Usuario cliente = usuarioRepository.findByCorreo(correoCliente)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "No se encontró la cuenta del cliente."));

        OrdenReparacion orden = ordenRepository.findById(idOrden)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                        "La solicitud no existe."));

        // Comprobar que la orden pertenece al cliente autenticado
        if (!orden.getEquipo().getCliente().getIdUsuario()
                .equals(cliente.getIdUsuario())) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "No tenés permiso para consultar esta solicitud.");
        }

        // Inicializar los datos del equipo para la vista
        orden.getEquipo().getTipo();
        orden.getEquipo().getMarca();
        orden.getEquipo().getModelo();
        orden.getEquipo().getNumeroSerie();

        return orden;
    }

}
