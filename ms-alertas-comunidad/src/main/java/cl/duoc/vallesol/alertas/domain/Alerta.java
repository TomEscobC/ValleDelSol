package cl.duoc.vallesol.alertas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia de una alerta comunitaria.
 * Se mapea a la tabla 'alertas' en PostgreSQL.
 */
@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 600)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 150)
    private String zona;

    /** Id del reporte ciudadano que origino la alerta (trazabilidad entre microservicios). */
    private Long reporteOrigenId;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    public Alerta() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public NivelAlerta getNivel() { return nivel; }
    public void setNivel(NivelAlerta nivel) { this.nivel = nivel; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public Long getReporteOrigenId() { return reporteOrigenId; }
    public void setReporteOrigenId(Long reporteOrigenId) { this.reporteOrigenId = reporteOrigenId; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
