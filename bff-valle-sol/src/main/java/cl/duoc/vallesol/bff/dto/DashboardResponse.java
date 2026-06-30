package cl.duoc.vallesol.bff.dto;

import java.util.List;
import java.util.Map;

/**
 * Datos agregados por el BFF para el Dashboard del Centro de Comando.
 * Reune informacion de los microservicios de Reportes y Alertas en una sola respuesta.
 */
public class DashboardResponse {

    private long totalReportes;
    private long reportesCriticos;
    private long alertasActivas;
    private Map<String, Long> reportesPorGravedad;
    private Map<String, Long> reportesPorEstado;
    private List<ReporteDto> ultimosReportes;
    private List<AlertaDto> alertasVigentes;

    public long getTotalReportes() { return totalReportes; }
    public void setTotalReportes(long totalReportes) { this.totalReportes = totalReportes; }

    public long getReportesCriticos() { return reportesCriticos; }
    public void setReportesCriticos(long reportesCriticos) { this.reportesCriticos = reportesCriticos; }

    public long getAlertasActivas() { return alertasActivas; }
    public void setAlertasActivas(long alertasActivas) { this.alertasActivas = alertasActivas; }

    public Map<String, Long> getReportesPorGravedad() { return reportesPorGravedad; }
    public void setReportesPorGravedad(Map<String, Long> reportesPorGravedad) { this.reportesPorGravedad = reportesPorGravedad; }

    public Map<String, Long> getReportesPorEstado() { return reportesPorEstado; }
    public void setReportesPorEstado(Map<String, Long> reportesPorEstado) { this.reportesPorEstado = reportesPorEstado; }

    public List<ReporteDto> getUltimosReportes() { return ultimosReportes; }
    public void setUltimosReportes(List<ReporteDto> ultimosReportes) { this.ultimosReportes = ultimosReportes; }

    public List<AlertaDto> getAlertasVigentes() { return alertasVigentes; }
    public void setAlertasVigentes(List<AlertaDto> alertasVigentes) { this.alertasVigentes = alertasVigentes; }
}
