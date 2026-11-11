package com.tecnitrack.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_reparacion")
public class OrdenReparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrden;

    @Column(name = "numero_orden", nullable = false,
            unique = true, length = 50)
    private String numeroOrden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico")
    private Usuario tecnico;

    @Column(name = "estado_actual", nullable = false, length = 50)
    private String estadoActual = "Por recibir";

    @Column(name = "falla_reportada", nullable = false, length = 2000)
    private String fallaReportada;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "condicion_recepcion", length = 1000)
    private String condicionRecepcion;

    @Column(length = 1000)
    private String accesorios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_recepcion")
    private Usuario adminRecepcion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "persona_retira", length = 150)
    private String personaRetira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_entrega")
    private Usuario adminEntrega;

    public OrdenReparacion() {
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getFallaReportada() {
        return fallaReportada;
    }

    public void setFallaReportada(String fallaReportada) {
        this.fallaReportada = fallaReportada;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getCondicionRecepcion() {
        return condicionRecepcion;
    }

    public void setCondicionRecepcion(String condicionRecepcion) {
        this.condicionRecepcion = condicionRecepcion;
    }

    public String getAccesorios() {
        return accesorios;
    }

    public void setAccesorios(String accesorios) {
        this.accesorios = accesorios;
    }

    public Usuario getAdminRecepcion() {
        return adminRecepcion;
    }

    public void setAdminRecepcion(Usuario adminRecepcion) {
        this.adminRecepcion = adminRecepcion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getPersonaRetira() {
        return personaRetira;
    }

    public void setPersonaRetira(String personaRetira) {
        this.personaRetira = personaRetira;
    }

    public Usuario getAdminEntrega() {
        return adminEntrega;
    }

    public void setAdminEntrega(Usuario adminEntrega) {
        this.adminEntrega = adminEntrega;
    }
}
