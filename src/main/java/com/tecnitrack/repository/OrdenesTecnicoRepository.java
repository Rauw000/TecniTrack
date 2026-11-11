package com.tecnitrack.repository;

import com.tecnitrack.domain.OrdenReparacion;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrdenesTecnicoRepository {
    private final EntityManager entityManager;

    public OrdenesTecnicoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<OrdenReparacion> listarAsignadas(Long idTecnico) {
        return entityManager.createQuery(
            "select o from OrdenReparacion o join fetch o.equipo e " +
            "where o.tecnico.idUsuario = :idTecnico " +
            "order by o.fechaSolicitud desc", OrdenReparacion.class)
            .setParameter("idTecnico", idTecnico)
            .getResultList();
    }

    public Optional<OrdenReparacion> buscarAsignada(Long idOrden, Long idTecnico) {
        return entityManager.createQuery(
            "select o from OrdenReparacion o join fetch o.equipo e " +
            "where o.idOrden = :idOrden and o.tecnico.idUsuario = :idTecnico", OrdenReparacion.class)
            .setParameter("idOrden", idOrden)
            .setParameter("idTecnico", idTecnico)
            .getResultStream().findFirst();
    }
}
