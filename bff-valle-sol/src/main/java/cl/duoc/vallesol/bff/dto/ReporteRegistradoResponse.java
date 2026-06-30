package cl.duoc.vallesol.bff.dto;

/**
 * Resultado del flujo de negocio "registrar reporte": el reporte creado y,
 * si correspondio (reporte critico), la alerta comunitaria generada.
 */
public class ReporteRegistradoResponse {

    private ReporteDto reporte;
    private boolean alertaGenerada;
    private AlertaDto alerta;

    public ReporteRegistradoResponse() {
    }

    public ReporteRegistradoResponse(ReporteDto reporte, boolean alertaGenerada, AlertaDto alerta) {
        this.reporte = reporte;
        this.alertaGenerada = alertaGenerada;
        this.alerta = alerta;
    }

    public ReporteDto getReporte() { return reporte; }
    public void setReporte(ReporteDto reporte) { this.reporte = reporte; }

    public boolean isAlertaGenerada() { return alertaGenerada; }
    public void setAlertaGenerada(boolean alertaGenerada) { this.alertaGenerada = alertaGenerada; }

    public AlertaDto getAlerta() { return alerta; }
    public void setAlerta(AlertaDto alerta) { this.alerta = alerta; }
}
