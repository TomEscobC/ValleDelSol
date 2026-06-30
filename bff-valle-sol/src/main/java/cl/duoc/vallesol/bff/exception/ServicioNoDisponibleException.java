package cl.duoc.vallesol.bff.exception;

/** Se lanza cuando un microservicio aguas abajo no responde (caida o timeout). */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
