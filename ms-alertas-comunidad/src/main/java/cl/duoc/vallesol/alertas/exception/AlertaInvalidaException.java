package cl.duoc.vallesol.alertas.exception;

/** Se lanza cuando una alerta viola una regla de negocio o sus datos son invalidos. */
public class AlertaInvalidaException extends RuntimeException {
    public AlertaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
