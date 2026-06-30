package cl.duoc.vallesol.alertas.exception;

/** Se lanza cuando se solicita una alerta que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
