package cl.duoc.vallesol.bff.web;

import cl.duoc.vallesol.bff.dto.*;
import cl.duoc.vallesol.bff.service.OrquestadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API del Backend For Frontend. Es la unica API que consume el frontend Angular.
 * Base: /api/bff
 */
@RestController
@RequestMapping("/api/bff")
public class BffController {

    private final OrquestadorService orquestador;

    public BffController(OrquestadorService orquestador) {
        this.orquestador = orquestador;
    }

    /** Flujo de negocio principal: registra un reporte y, si es critico, genera la alerta. */
    @PostMapping("/reportes")
    public ResponseEntity<ReporteRegistradoResponse> registrarReporte(
            @Valid @RequestBody CrearReporteBffRequest request) {
        ReporteRegistradoResponse respuesta = orquestador.registrarReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/reportes")
    public List<ReporteDto> listarReportes() {
        return orquestador.listarReportes();
    }

    @GetMapping("/alertas")
    public List<AlertaDto> listarAlertas() {
        return orquestador.listarAlertasActivas();
    }

    /** Datos agregados para el Dashboard del Centro de Comando. */
    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return orquestador.construirDashboard();
    }

    @GetMapping("/salud")
    public ResponseEntity<String> salud() {
        return ResponseEntity.ok("bff-valle-sol OK");
    }
}
