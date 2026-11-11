package com.tecnitrack.service;

import com.tecnitrack.domain.Diagnostico;
import com.tecnitrack.domain.OrdenReparacion;
import com.tecnitrack.domain.Usuario;
import com.tecnitrack.repository.DiagnosticoRepository;
import com.tecnitrack.repository.OrdenesTecnicoRepository;
import com.tecnitrack.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiagnosticoService {
    private final UsuarioRepository usuarioRepository;
    private final OrdenesTecnicoRepository ordenesTecnicoRepository;
    private final DiagnosticoRepository diagnosticoRepository;

    public DiagnosticoService(UsuarioRepository usuarioRepository,
            OrdenesTecnicoRepository ordenesTecnicoRepository,
            DiagnosticoRepository diagnosticoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ordenesTecnicoRepository = ordenesTecnicoRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    private Usuario tecnicoAutenticado(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (!Boolean.TRUE.equals(usuario.getActivo()) || usuario.getRol() == null ||
                !"Tecnico".equals(usuario.getRol().getNombre().replace("Técnico", "Tecnico"))) {
            throw new IllegalArgumentException("Acceso exclusivo para técnicos activos.");
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<OrdenReparacion> consultarAsignadas(String correo) {
        return ordenesTecnicoRepository.listarAsignadas(tecnicoAutenticado(correo).getIdUsuario());
    }

    @Transactional(readOnly = true)
    public OrdenReparacion consultarOrden(String correo, Long idOrden) {
        return ordenesTecnicoRepository.buscarAsignada(idOrden, tecnicoAutenticado(correo).getIdUsuario())
            .orElseThrow(() -> new IllegalArgumentException("La orden no existe o no está asignada a este técnico."));
    }

    @Transactional(readOnly = true)
    public Optional<Diagnostico> consultarDiagnostico(String correo, Long idOrden) {
        consultarOrden(correo, idOrden);
        return diagnosticoRepository.findByOrden_IdOrden(idOrden);
    }

    @Transactional
    public void registrar(String correo, Long idOrden, String problema,
            String detalle, String reparacion) {
        Usuario tecnico = tecnicoAutenticado(correo);
        OrdenReparacion orden = ordenesTecnicoRepository.buscarAsignada(idOrden, tecnico.getIdUsuario())
            .orElseThrow(() -> new IllegalArgumentException("La orden no está asignada a este técnico."));
        String p = validar(problema, 2000, "problema identificado");
        String d = validar(detalle, 4000, "diagnóstico técnico");
        String r = validar(reparacion, 2000, "reparación recomendada");
        if (diagnosticoRepository.existsByOrden_IdOrden(idOrden)) {
            throw new IllegalArgumentException("La orden ya tiene un diagnóstico registrado.");
        }
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setOrden(orden);
        diagnostico.setTecnico(tecnico);
        diagnostico.setProblemaIdentificado(p);
        diagnostico.setDiagnosticoTecnico(d);
        diagnostico.setReparacionRecomendada(r);
        diagnostico.setFechaDiagnostico(LocalDateTime.now());
        diagnosticoRepository.saveAndFlush(diagnostico);
    }

    private String validar(String valor, int maximo, String nombre) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Debe completar el campo " + nombre + ".");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new IllegalArgumentException("El campo " + nombre + " supera " + maximo + " caracteres.");
        }
        return limpio;
    }
}
