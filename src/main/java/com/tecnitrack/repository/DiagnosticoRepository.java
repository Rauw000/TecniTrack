package com.tecnitrack.repository;

import com.tecnitrack.domain.Diagnostico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    boolean existsByOrden_IdOrden(Long idOrden);
    Optional<Diagnostico> findByOrden_IdOrden(Long idOrden);
}
