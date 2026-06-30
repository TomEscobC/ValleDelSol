package cl.duoc.vallesol.alertas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud para generar una alerta a partir de un reporte ciudadano critico.
 * La envia el BFF al orquestar el flujo de negocio.
 */
public class GenerarAlertaRequest {

    @NotNull(message = "El id del reporte de origen es obligatorio")
    private Long reporteOrigenId;

    @NotBlank(message = "La gravedad del reporte es obligatoria")
    private String gravedad; // BAJA, MEDIA, ALTA, CRITICA

    @NotBlank(message = "La zona afectada es obligatoria")
    private String zona;

    private String tipoEmergencia;

    public Long getReporteOrigenId() { return reporteOrigenId; }
    public void setReporteOrigenId(Long reporteOrigenId) { this.reporteOrigenId = reporteOrigenId; }

    public String getGravedad() { return gravedad; }
    public void setGravedad(String gravedad) { this.gravedad = gravedad; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getTipoEmergencia() { return tipoEmergencia; }
    public void setTipoEmergencia(String tipoEmergencia) { this.tipoEmergencia = tipoEmergencia; }
}
