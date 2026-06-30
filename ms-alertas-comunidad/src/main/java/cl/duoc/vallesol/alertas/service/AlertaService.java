package cl.duoc.vallesol.alertas.service;

import cl.duoc.vallesol.alertas.domain.Alerta;
import cl.duoc.vallesol.alertas.domain.NivelAlerta;
import cl.duoc.vallesol.alertas.dto.GenerarAlertaRequest;
import cl.duoc.vallesol.alertas.exception.AlertaInvalidaException;
import cl.duoc.vallesol.alertas.exception.RecursoNoEncontradoException;
import cl.duoc.vallesol.alertas.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica de negocio del Motor de Alertas a la Comunidad.
 *
 * Regla central: la gravedad del reporte determina el nivel de la alerta.
 *   CRITICA -> EVACUACION
 *   ALTA    -> PREVENTIVA
 *   otra    -> INFORMATIVA
 */
@Service
public class AlertaService {

    private final AlertaRepository repository;

    public AlertaService(AlertaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Alerta generarDesdeReporte(GenerarAlertaRequest request) {
        if (request == null || request.getReporteOrigenId() == null) {
            throw new AlertaInvalidaException("Se requiere el id del reporte de origen");
        }
        if (request.getZona() == null || request.getZona().isBlank()) {
            throw new AlertaInvalidaException("La zona afectada es obligatoria");
        }

        NivelAlerta nivel = mapearNivel(request.getGravedad());

        Alerta alerta = new Alerta();
        alerta.setNivel(nivel);
        alerta.setZona(request.getZona());
        alerta.setReporteOrigenId(request.getReporteOrigenId());
        alerta.setTitulo(construirTitulo(nivel, request.getZona()));
        alerta.setMensaje(construirMensaje(nivel, request));
        alerta.setActiva(true);
        alerta.setFechaEmision(LocalDateTime.now());

        return repository.save(alerta);
    }

    /** Mapea la gravedad de un reporte al nivel de alerta. Regla de negocio clave (prueba unitaria). */
    public NivelAlerta mapearNivel(String gravedad) {
        if (gravedad == null) {
            throw new AlertaInvalidaException("La gravedad es obligatoria para definir el nivel");
        }
        return switch (gravedad.trim().toUpperCase()) {
            case "CRITICA" -> NivelAlerta.EVACUACION;
            case "ALTA" -> NivelAlerta.PREVENTIVA;
            default -> NivelAlerta.INFORMATIVA;
        };
    }

    @Transactional(readOnly = true)
    public List<Alerta> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Alerta> listarActivas() {
        return repository.findByActivaTrueOrderByFechaEmisionDesc();
    }

    @Transactional(readOnly = true)
    public long contarActivas() {
        return repository.countByActivaTrue();
    }

    @Transactional
    public Alerta desactivar(Long id) {
        Alerta alerta = repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe la alerta con id " + id));
        alerta.setActiva(false);
        return repository.save(alerta);
    }

    private String construirTitulo(NivelAlerta nivel, String zona) {
        return switch (nivel) {
            case EVACUACION -> "EVACUACION INMEDIATA - " + zona;
            case PREVENTIVA -> "ALERTA PREVENTIVA - " + zona;
            case INFORMATIVA -> "INFORMACION - " + zona;
        };
    }

    private String construirMensaje(NivelAlerta nivel, GenerarAlertaRequest req) {
        String tipo = req.getTipoEmergencia() == null ? "emergencia" : req.getTipoEmergencia();
        return switch (nivel) {
            case EVACUACION -> "Se ordena evacuacion inmediata del sector " + req.getZona()
                + " por " + tipo + " de gravedad CRITICA. Siga las rutas de evacuacion senaladas.";
            case PREVENTIVA -> "Se recomienda extremar precauciones en " + req.getZona()
                + " por " + tipo + " de gravedad ALTA. Mantengase atento a nuevas instrucciones.";
            case INFORMATIVA -> "Se informa a la comunidad de " + req.getZona()
                + " sobre un evento de " + tipo + ". Sin riesgo inminente por el momento.";
        };
    }
}
