package cl.duoc.vallesol.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/** Representacion de la alerta tal como la expone el microservicio de Alertas. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertaDto {

    private Long id;
    private String titulo;
    private String mensaje;
    private String nivel;
    private String zona;
    private Long reporteOrigenId;
    private boolean activa;
    private LocalDateTime fechaEmision;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public Long getReporteOrigenId() { return reporteOrigenId; }
    public void setReporteOrigenId(Long reporteOrigenId) { this.reporteOrigenId = reporteOrigenId; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
