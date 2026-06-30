package cl.duoc.vallesol.alertas.web;

import cl.duoc.vallesol.alertas.domain.Alerta;
import cl.duoc.vallesol.alertas.dto.GenerarAlertaRequest;
import cl.duoc.vallesol.alertas.service.AlertaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del Motor de Alertas a la Comunidad.
 * Base: /api/alertas
 */
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    @PostMapping("/generar")
    public ResponseEntity<Alerta> generar(@Valid @RequestBody GenerarAlertaRequest request) {
        Alerta alerta = service.generarDesdeReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alerta);
    }

    @GetMapping
    public List<Alerta> listar() {
        return service.listar();
    }

    @GetMapping("/activas")
    public List<Alerta> activas() {
        return service.listarActivas();
    }

    @PatchMapping("/{id}/desactivar")
    public Alerta desactivar(@PathVariable Long id) {
        return service.desactivar(id);
    }

    @GetMapping("/salud")
    public ResponseEntity<String> salud() {
        return ResponseEntity.ok("ms-alertas-comunidad OK");
    }
}
