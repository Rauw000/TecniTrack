package com.tecnitrack.repository;

import com.tecnitrack.domain.OrdenReparacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenReparacionRepository
        extends JpaRepository<OrdenReparacion, Long> {

    // Consultar órdenes de un cliente, de la más reciente a la más antigua
    List<OrdenReparacion>
            findByEquipo_Cliente_IdUsuarioOrderByFechaSolicitudDesc(
                    Long idUsuario);

    // Consultar órdenes según su estado
    List<OrdenReparacion> findByEstadoActual(String estadoActual);

    // Contar órdenes según su estado
    long countByEstadoActual(String estadoActual);

    // Buscar una orden por su número único
    boolean existsByNumeroOrden(String numeroOrden);
}
