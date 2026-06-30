package cl.duoc.vallesol.bff.dto;

/** Cuerpo que el BFF envia al microservicio de Alertas para generar una alerta. */
public class GenerarAlertaBffRequest {

    private Long reporteOrigenId;
    private String gravedad;
    private String zona;
    private String tipoEmergencia;

    public GenerarAlertaBffRequest() {
    }

    public GenerarAlertaBffRequest(Long reporteOrigenId, String gravedad, String zona, String tipoEmergencia) {
        this.reporteOrigenId = reporteOrigenId;
        this.gravedad = gravedad;
        this.zona = zona;
        this.tipoEmergencia = tipoEmergencia;
    }

    public Long getReporteOrigenId() { return reporteOrigenId; }
    public void setReporteOrigenId(Long reporteOrigenId) { this.reporteOrigenId = reporteOrigenId; }

    public String getGravedad() { return gravedad; }
    public void setGravedad(String gravedad) { this.gravedad = gravedad; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getTipoEmergencia() { return tipoEmergencia; }
    public void setTipoEmergencia(String tipoEmergencia) { this.tipoEmergencia = tipoEmergencia; }
}
