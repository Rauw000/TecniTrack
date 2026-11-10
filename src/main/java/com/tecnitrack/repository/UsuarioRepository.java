package com.tecnitrack.repository;

import com.tecnitrack.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    long countByRol_NombreAndActivoTrue(String nombreRol);

    // Contar todos los usuarios activos
    long countByActivoTrue();

    // Obtener todos los usuarios activos
    List<Usuario> findByActivoTrue();

    // Bloquear el registro del rol Administrador
    // durante operaciones que afectan a los administradores
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Rol r WHERE r.nombre = 'Administrador'")
    Optional<com.tecnitrack.domain.Rol> bloquearRolAdministrador();

}
