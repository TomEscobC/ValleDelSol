package cl.duoc.vallesol.reportes.exception;

/** Se lanza cuando se solicita un reporte que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
