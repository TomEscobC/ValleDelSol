package cl.duoc.vallesol.reportes.web;

import cl.duoc.vallesol.reportes.exception.RecursoNoEncontradoException;
import cl.duoc.vallesol.reportes.exception.ReporteInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Traduce las excepciones de negocio y de validacion a respuestas HTTP coherentes.
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

    @ExceptionHandler(ReporteInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarReporteInvalido(ReporteInvalidoException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), null);
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
