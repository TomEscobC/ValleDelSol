package cl.duoc.vallesol.bff.service;

import cl.duoc.vallesol.bff.client.AlertasClient;
import cl.duoc.vallesol.bff.client.ReportesClient;
import cl.duoc.vallesol.bff.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas UNITARIAS de la orquestacion del BFF. Los clientes hacia los microservicios
 * se simulan con Mockito para aislar la logica de orquestacion y agregacion.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrquestadorService - flujo de negocio y agregacion (pruebas unitarias)")
class OrquestadorServiceTest {

    @Mock private ReportesClient reportesClient;
    @Mock private AlertasClient alertasClient;
    @InjectMocks private OrquestadorService orquestador;

    private CrearReporteBffRequest request(String gravedad, String sector) {
        CrearReporteBffRequest r = new CrearReporteBffRequest();
        r.setNombreCiudadano("Camila Rojas");
        r.setTipo("INCENDIO_FORESTAL");
        r.setDescripcion("Foco activo con llamas cerca de viviendas.");
        r.setLatitud(-34.70);
        r.setLongitud(-71.01);
        r.setSectorReferencia(sector);
        r.setGravedad(gravedad);
        return r;
    }

    private ReporteDto reporteDto(Long id, String gravedad) {
        ReporteDto dto = new ReporteDto();
        dto.setId(id);
        dto.setGravedad(gravedad);
        dto.setEstado("PENDIENTE");
        dto.setFechaCreacion(LocalDateTime.now());
        return dto;
    }

    @Test
    @DisplayName("CP-U15: reporte CRITICO dispara la generacion de una alerta comunitaria")
    void registrarReporte_critico_generaAlerta() {
        when(reportesClient.crear(any())).thenReturn(reporteDto(1L, "CRITICA"));
        AlertaDto alerta = new AlertaDto();
        alerta.setNivel("EVACUACION");
        when(alertasClient.generar(any())).thenReturn(alerta);

        ReporteRegistradoResponse resp = orquestador.registrarReporte(request("CRITICA", "Curva El Venado"));

        assertThat(resp.isAlertaGenerada()).isTrue();
        assertThat(resp.getAlerta()).isNotNull();
        verify(alertasClient, times(1)).generar(any());
    }

    @Test
    @DisplayName("CP-U16: reporte NO critico (MEDIA) NO genera alerta")
    void registrarReporte_noCritico_noGeneraAlerta() {
        when(reportesClient.crear(any())).thenReturn(reporteDto(2L, "MEDIA"));

        ReporteRegistradoResponse resp = orquestador.registrarReporte(request("MEDIA", "Camino El Roble"));

        assertThat(resp.isAlertaGenerada()).isFalse();
        assertThat(resp.getAlerta()).isNull();
        verify(alertasClient, never()).generar(any());
    }

    @Test
    @DisplayName("CP-U17: el dashboard agrega correctamente totales, criticos y conteo por gravedad")
    void construirDashboard_agregaCorrectamente() {
        when(reportesClient.listar()).thenReturn(List.of(
            reporteDto(1L, "CRITICA"),
            reporteDto(2L, "ALTA"),
            reporteDto(3L, "MEDIA"),
            reporteDto(4L, "BAJA")
        ));
        AlertaDto a = new AlertaDto();
        a.setActiva(true);
        when(alertasClient.listarActivas()).thenReturn(List.of(a, a));

        DashboardResponse dash = orquestador.construirDashboard();

        assertThat(dash.getTotalReportes()).isEqualTo(4);
        assertThat(dash.getReportesCriticos()).isEqualTo(2); // CRITICA + ALTA
        assertThat(dash.getAlertasActivas()).isEqualTo(2);
        assertThat(dash.getReportesPorGravedad().get("CRITICA")).isEqualTo(1);
        assertThat(dash.getReportesPorGravedad().get("MEDIA")).isEqualTo(1);
    }
}
