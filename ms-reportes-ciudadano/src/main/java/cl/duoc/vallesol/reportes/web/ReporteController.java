package cl.duoc.vallesol.reportes.web;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import cl.duoc.vallesol.reportes.domain.Reporte;
import cl.duoc.vallesol.reportes.dto.CambiarEstadoRequest;
import cl.duoc.vallesol.reportes.dto.CrearReporteRequest;
import cl.duoc.vallesol.reportes.service.ReporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * API REST del microservicio de Reporte Ciudadano.
 * Base: /api/reportes
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reporte> crear(@Valid @RequestBody CrearReporteRequest request,
                                         UriComponentsBuilder uriBuilder) {
        Reporte creado = service.crear(request);
        URI ubicacion = uriBuilder.path("/api/reportes/{id}")
            .buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping
    public List<Reporte> listar(@RequestParam(required = false) EstadoReporte estado) {
        return estado == null ? service.listar() : service.listarPorEstado(estado);
    }

    @GetMapping("/ultimos")
    public List<Reporte> ultimos() {
        return service.ultimosReportes();
    }

    @GetMapping("/{id}")
    public Reporte obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public Reporte cambiarEstado(@PathVariable Long id,
                                 @Valid @RequestBody CambiarEstadoRequest request) {
        return service.cambiarEstado(id, request.getNuevoEstado());
    }

    @GetMapping("/salud")
    public ResponseEntity<String> salud() {
        return ResponseEntity.status(HttpStatus.OK).body("ms-reportes-ciudadano OK");
    }
}
