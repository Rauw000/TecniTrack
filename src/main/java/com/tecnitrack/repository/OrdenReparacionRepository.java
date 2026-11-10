package com.tecnitrack.repository;

import com.tecnitrack.domain.OrdenReparacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface OrdenReparacionRepository
        extends JpaRepository<OrdenReparacion, Long> {

    // Consultar órdenes de un cliente
    List<OrdenReparacion>
            findByEquipo_Cliente_IdUsuarioOrderByFechaSolicitudDesc(
                    Long idUsuario);

    // Consultar órdenes según su estado
    List<OrdenReparacion> findByEstadoActual(String estadoActual);

    // Contar órdenes según su estado
    long countByEstadoActual(String estadoActual);

    // Obtener las últimas 5 órdenes registradas
    List<OrdenReparacion> findTop5ByOrderByFechaSolicitudDesc();

    // Buscar una orden por su número único
    boolean existsByNumeroOrden(String numeroOrden);

    // Todas las órdenes ordenadas por fecha
    List<OrdenReparacion> findAllByOrderByFechaSolicitudDesc();

    // Órdenes filtradas por estado
    List<OrdenReparacion> findByEstadoActualOrderByFechaSolicitudDesc(
            String estadoActual);

    // Bloquear una orden mientras se registra su recepción
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenReparacion o WHERE o.idOrden = :idOrden")
    Optional<OrdenReparacion> buscarOrdenParaRecepcion(
            @Param("idOrden") Long idOrden);

}
