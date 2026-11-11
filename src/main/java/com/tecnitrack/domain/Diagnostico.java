package com.tecnitrack.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostico", uniqueConstraints = @UniqueConstraint(name = "uk_diagnostico_orden", columnNames = "id_orden"))
public class Diagnostico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagnostico")
    private Long idDiagnostico;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden", nullable = false, unique = true)
    private OrdenReparacion orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Usuario tecnico;

    @Column(name = "problema_identificado", nullable = false, length = 2000)
    private String problemaIdentificado;

    @Column(name = "diagnostico_tecnico", nullable = false, length = 4000)
    private String diagnosticoTecnico;

    @Column(name = "reparacion_recomendada", nullable = false, length = 2000)
    private String reparacionRecomendada;

    @Column(name = "fecha_diagnostico", nullable = false)
    private LocalDateTime fechaDiagnostico;

    public Long getIdDiagnostico() { return idDiagnostico; }
    public void setIdDiagnostico(Long idDiagnostico) { this.idDiagnostico = idDiagnostico; }
    public OrdenReparacion getOrden() { return orden; }
    public void setOrden(OrdenReparacion orden) { this.orden = orden; }
    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }
    public String getProblemaIdentificado() { return problemaIdentificado; }
    public void setProblemaIdentificado(String problemaIdentificado) { this.problemaIdentificado = problemaIdentificado; }
    public String getDiagnosticoTecnico() { return diagnosticoTecnico; }
    public void setDiagnosticoTecnico(String diagnosticoTecnico) { this.diagnosticoTecnico = diagnosticoTecnico; }
    public String getReparacionRecomendada() { return reparacionRecomendada; }
    public void setReparacionRecomendada(String reparacionRecomendada) { this.reparacionRecomendada = reparacionRecomendada; }
    public LocalDateTime getFechaDiagnostico() { return fechaDiagnostico; }
    public void setFechaDiagnostico(LocalDateTime fechaDiagnostico) { this.fechaDiagnostico = fechaDiagnostico; }
}
