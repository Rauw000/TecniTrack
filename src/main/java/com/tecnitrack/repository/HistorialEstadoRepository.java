
package com.tecnitrack.repository;

import com.tecnitrack.domain.HistorialEstado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoRepository
        extends JpaRepository<HistorialEstado, Long> {

    // Consultar historial de una orden,
    // mostrando primero los cambios más recientes.
    List<HistorialEstado>
            findByOrden_IdOrdenOrderByFechaCambioDesc(
                    Long idOrden);
}
