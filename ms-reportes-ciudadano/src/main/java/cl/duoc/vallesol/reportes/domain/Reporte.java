package cl.duoc.vallesol.reportes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia de un reporte ciudadano de emergencia.
 * Se mapea a la tabla 'reportes' en PostgreSQL.
 */
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreCiudadano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEmergencia tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(length = 150)
    private String sectorReferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Gravedad gravedad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoReporte estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public Reporte() {
    }

    /** Folio legible derivado del id (ej: REP-0007). Util para UI y trazabilidad. */
    @Transient
    public String getFolio() {
        return id == null ? "REP-PENDIENTE" : String.format("REP-%04d", id);
    }

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCiudadano() { return nombreCiudadano; }
    public void setNombreCiudadano(String nombreCiudadano) { this.nombreCiudadano = nombreCiudadano; }

    public TipoEmergencia getTipo() { return tipo; }
    public void setTipo(TipoEmergencia tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getSectorReferencia() { return sectorReferencia; }
    public void setSectorReferencia(String sectorReferencia) { this.sectorReferencia = sectorReferencia; }

    public Gravedad getGravedad() { return gravedad; }
    public void setGravedad(Gravedad gravedad) { this.gravedad = gravedad; }

    public EstadoReporte getEstado() { return estado; }
    public void setEstado(EstadoReporte estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
