package cl.duoc.vallesol.reportes.exception;

/** Se lanza cuando un reporte viola una regla de negocio (datos o transicion de estado invalida). */
public class ReporteInvalidoException extends RuntimeException {
    public ReporteInvalidoException(String mensaje) {
        super(mensaje);
    }
}
