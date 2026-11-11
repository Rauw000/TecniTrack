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
import com.tecnitrack.domain.HistorialEstado;
import com.tecnitrack.repository.HistorialEstadoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenReparacionService {

    private final EquipoRepository equipoRepository;
    private final OrdenReparacionRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEstadoRepository historialRepository;

    public OrdenReparacionService(
            EquipoRepository equipoRepository,
            OrdenReparacionRepository ordenRepository,
            UsuarioRepository usuarioRepository,
            HistorialEstadoRepository historialRepository) {

        this.equipoRepository = equipoRepository;
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
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

    @Transactional
    public OrdenReparacion registrarRecepcion(
            Long idOrden,
            String correoAdministrador,
            String condicionRecepcion,
            String accesorios) {

        // 1. Validar campos obligatorios
        validarCampo(condicionRecepcion, "La condición física");
        validarCampo(accesorios, "Los accesorios");

        validarLongitud(condicionRecepcion, 2000,
                "La condición física");
        validarLongitud(accesorios, 1000,
                "Los accesorios");

        // 2. Identificar al administrador autenticado
        Usuario administrador = usuarioRepository
                .findByCorreo(correoAdministrador)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró al administrador."));

        if (!Boolean.TRUE.equals(administrador.getActivo())
                || administrador.getRol() == null
                || !"Administrador".equals(
                        administrador.getRol().getNombre())) {

            throw new IllegalArgumentException(
                    "Solo un administrador activo puede recibir equipos.");
        }

        // 3. Buscar la orden
        OrdenReparacion orden = ordenRepository.buscarOrdenParaRecepcion(idOrden)
                .orElseThrow(() -> new IllegalArgumentException(
                "La orden de reparación no existe."));

        // 4. Evitar recibir una orden dos veces
        if (!"Por recibir".equals(orden.getEstadoActual())
                || orden.getFechaRecepcion() != null) {

            throw new IllegalArgumentException(
                    "Esta orden ya fue recibida o no está pendiente de recepción.");
        }

        // 5. Registrar los datos de recepción
        String estadoAnterior = orden.getEstadoActual();

        orden.setCondicionRecepcion(condicionRecepcion.trim());
        orden.setAccesorios(accesorios.trim());
        orden.setFechaRecepcion(LocalDateTime.now());
        orden.setAdminRecepcion(administrador);
        orden.setEstadoActual("Recibido");

        // 6. Guardar el cambio de estado
        HistorialEstado historial = new HistorialEstado();

        historial.setOrden(orden);
        historial.setUsuario(administrador);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo("Recibido");
        historial.setFechaCambio(LocalDateTime.now());

        historialRepository.save(historial);

        // 7. Guardar la orden
        return ordenRepository.save(orden);
    }

}
