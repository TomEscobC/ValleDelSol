package cl.duoc.vallesol.reportes.domain;

/**
 * Nivel de gravedad de un reporte de emergencia.
 * Las gravedades ALTA y CRITICA se consideran criticas para el negocio:
 * disparan la generacion automatica de una alerta a la comunidad.
 */
public enum Gravedad {
    BAJA,
    MEDIA,
    ALTA,
    CRITICA;

    /** Regla de negocio central: define que gravedades requieren alerta comunitaria. */
    public boolean esCritica() {
        return this == ALTA || this == CRITICA;
    }
}
