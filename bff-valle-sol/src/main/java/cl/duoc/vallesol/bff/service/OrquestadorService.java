package cl.duoc.vallesol.bff.service;

import cl.duoc.vallesol.bff.client.AlertasClient;
import cl.duoc.vallesol.bff.client.ReportesClient;
import cl.duoc.vallesol.bff.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquesta el flujo de negocio principal de Valle del Sol y agrega datos para el dashboard.
 *
 * Flujo critico (business core):
 *   1. El ciudadano registra un reporte  -> se persiste en ms-reportes.
 *   2. Si el reporte es CRITICO (gravedad ALTA o CRITICA) -> se genera automaticamente
 *      una alerta comunitaria en ms-alertas.
 *   3. Se devuelve al frontend el reporte y, si correspondio, la alerta.
 */
@Service
public class OrquestadorService {

    /** Gravedades que disparan una alerta comunitaria (regla de negocio compartida). */
    private static final Set<String> GRAVEDADES_CRITICAS = Set.of("ALTA", "CRITICA");

    private final ReportesClient reportesClient;
    private final AlertasClient alertasClient;

    public OrquestadorService(ReportesClient reportesClient, AlertasClient alertasClient) {
        this.reportesClient = reportesClient;
        this.alertasClient = alertasClient;
    }

    public ReporteRegistradoResponse registrarReporte(CrearReporteBffRequest request) {
        // Paso 1: persistir el reporte en el microservicio de Reportes.
        ReporteDto reporte = reportesClient.crear(request);

        // Paso 2: si es critico, generar alerta comunitaria.
        if (esCritico(request.getGravedad())) {
            String zona = (request.getSectorReferencia() == null || request.getSectorReferencia().isBlank())
                ? "Sector no especificado"
                : request.getSectorReferencia();

            GenerarAlertaBffRequest alertaReq = new GenerarAlertaBffRequest(
                reporte.getId(), request.getGravedad(), zona, request.getTipo());

            AlertaDto alerta = alertasClient.generar(alertaReq);
            return new ReporteRegistradoResponse(reporte, true, alerta);
        }

        return new ReporteRegistradoResponse(reporte, false, null);
    }

    private boolean esCritico(String gravedad) {
        return gravedad != null && GRAVEDADES_CRITICAS.contains(gravedad.trim().toUpperCase());
    }

    public List<ReporteDto> listarReportes() {
        return reportesClient.listar();
    }

    public List<AlertaDto> listarAlertasActivas() {
        return alertasClient.listarActivas();
    }

    /** Agrega los datos de ambos microservicios para el Dashboard del Centro de Comando. */
    public DashboardResponse construirDashboard() {
        List<ReporteDto> reportes = reportesClient.listar();
        List<AlertaDto> alertas = alertasClient.listarActivas();

        DashboardResponse dash = new DashboardResponse();
        dash.setTotalReportes(reportes.size());
        dash.setReportesCriticos(reportes.stream().filter(r -> esCritico(r.getGravedad())).count());
        dash.setAlertasActivas(alertas.size());

        dash.setReportesPorGravedad(reportes.stream()
            .filter(r -> r.getGravedad() != null)
            .collect(Collectors.groupingBy(ReporteDto::getGravedad, Collectors.counting())));

        dash.setReportesPorEstado(reportes.stream()
            .filter(r -> r.getEstado() != null)
            .collect(Collectors.groupingBy(ReporteDto::getEstado, Collectors.counting())));

        dash.setUltimosReportes(reportes.stream()
            .sorted((a, b) -> {
                if (a.getFechaCreacion() == null) return 1;
                if (b.getFechaCreacion() == null) return -1;
                return b.getFechaCreacion().compareTo(a.getFechaCreacion());
            })
            .limit(5)
            .collect(Collectors.toList()));

        dash.setAlertasVigentes(alertas);
        return dash;
    }
}
