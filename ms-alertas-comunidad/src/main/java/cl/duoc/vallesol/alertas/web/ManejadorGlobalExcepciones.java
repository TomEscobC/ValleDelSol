package cl.duoc.vallesol.alertas.web;

import cl.duoc.vallesol.alertas.exception.AlertaInvalidaException;
import cl.duoc.vallesol.alertas.exception.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                e -> e.getField(),
                e -> e.getDefaultMessage() == null ? "valor invalido" : e.getDefaultMessage(),
                (a, b) -> a));
        return construir(HttpStatus.BAD_REQUEST, "Datos de la alerta invalidos", errores);
    }

    @ExceptionHandler(AlertaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarAlertaInvalida(AlertaInvalidaException ex) {
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
