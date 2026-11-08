package com.tecnitrack.repository;

import com.tecnitrack.domain.Servicio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Buscar servicios activos para mostrarlos al público
    List<Servicio> findByActivoTrueOrderByNombreAsc();

    // Buscar un servicio por su nombre
    Optional<Servicio> findByNombreIgnoreCase(String nombre);

    // Verificar nombres duplicados
    boolean existsByNombreIgnoreCase(String nombre);
}
