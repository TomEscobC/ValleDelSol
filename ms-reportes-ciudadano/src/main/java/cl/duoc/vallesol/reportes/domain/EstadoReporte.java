package cl.duoc.vallesol.reportes.domain;

import java.util.Set;
import java.util.Map;

/**
 * Estados del ciclo de vida de un reporte ciudadano.
 *
 * Transiciones permitidas (maquina de estados del negocio):
 *   PENDIENTE   -> VALIDADO, DESCARTADO
 *   VALIDADO    -> EN_ATENCION, DESCARTADO
 *   EN_ATENCION -> RESUELTO
 *   RESUELTO    -> (estado final)
 *   DESCARTADO  -> (estado final)
 */
public enum EstadoReporte {
    PENDIENTE,
    VALIDADO,
    EN_ATENCION,
    RESUELTO,
    DESCARTADO;

    private static final Map<EstadoReporte, Set<EstadoReporte>> TRANSICIONES = Map.of(
        PENDIENTE, Set.of(VALIDADO, DESCARTADO),
        VALIDADO, Set.of(EN_ATENCION, DESCARTADO),
        EN_ATENCION, Set.of(RESUELTO),
        RESUELTO, Set.of(),
        DESCARTADO, Set.of()
    );

    /** Indica si es valido pasar de este estado al estado destino. */
    public boolean puedeTransicionarA(EstadoReporte destino) {
        return TRANSICIONES.getOrDefault(this, Set.of()).contains(destino);
    }
}
