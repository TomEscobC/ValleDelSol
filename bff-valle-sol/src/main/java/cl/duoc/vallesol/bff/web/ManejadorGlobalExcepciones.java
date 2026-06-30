package cl.duoc.vallesol.bff.web;

import cl.duoc.vallesol.bff.exception.ServicioNoDisponibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Traduce errores de validacion del BFF y errores provenientes de los microservicios
 * a respuestas HTTP coherentes para el frontend.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                e -> e.getField(),
                e -> e.getDefaultMessage() == null ? "valor invalido" : e.getDefaultMessage(),
                (a, b) -> a));
        return construir(HttpStatus.BAD_REQUEST, "Datos del reporte invalidos", errores);
    }

    /** Propaga el codigo de error que devolvio un microservicio (ej. 400 de validacion). */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorAguasAbajo(HttpStatusCodeException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return construir(status, "Error reportado por un microservicio: " + ex.getStatusText(), null);
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarServicioCaido(ServicioNoDisponibleException ex) {
        return construir(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus status, String mensaje, Object detalle) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        if (detalle != null) {
            cuerpo.put("detalle", detalle);
        }
        return ResponseEntity.status(status).body(cuerpo);
    }
}
