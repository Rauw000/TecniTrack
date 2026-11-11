/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.tecnitrack.repository;

import com.tecnitrack.domain.Solicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository
        extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByUsuario_IdUsuarioOrderByFechaCreacionDesc(
            Long idUsuario
    );
}
