package cl.duoc.vallesol.reportes.config;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.Reporte;
import cl.duoc.vallesol.reportes.domain.TipoEmergencia;
import cl.duoc.vallesol.reportes.repository.ReporteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Carga reportes de ejemplo para la demo/video, solo si la tabla esta vacia.
 * No se ejecuta bajo el perfil 'test' para no interferir con las pruebas de integracion.
 */
@Component
@Profile("!test")
public class CargadorDatosDemo implements CommandLineRunner {

    private final ReporteRepository repository;

    public CargadorDatosDemo(ReporteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(crear("Camila Rojas", TipoEmergencia.INCENDIO_FORESTAL,
            "Foco activo con llamas de gran altura cerca de viviendas en la Curva El Venado.",
            -34.706, -71.012, "Curva El Venado, Ruta 5", Gravedad.CRITICA, EstadoReporte.EN_ATENCION, 2));
        repository.save(crear("Jorge Munoz", TipoEmergencia.COLUMNA_DE_HUMO,
            "Columna de humo densa avanzando hacia el sector de Los Pinos.",
            -34.712, -71.030, "Cruce Los Pinos", Gravedad.ALTA, EstadoReporte.VALIDADO, 1));
        repository.save(crear("Valentina Soto", TipoEmergencia.QUEMA_NO_AUTORIZADA,
            "Vecino realizando quema de ramas sin autorizacion en terreno seco.",
            -34.698, -71.045, "Camino El Roble", Gravedad.MEDIA, EstadoReporte.PENDIENTE, 0));
        repository.save(crear("Pedro Lagos", TipoEmergencia.FOCO_REACTIVADO,
            "Reaparece humo en zona que se daba por controlada ayer en la tarde.",
            -34.720, -71.005, "Quebrada San Jorge", Gravedad.ALTA, EstadoReporte.PENDIENTE, 0));
    }

    private Reporte crear(String ciudadano, TipoEmergencia tipo, String desc,
                          double lat, double lon, String sector,
                          Gravedad gravedad, EstadoReporte estado, int horasAtras) {
        Reporte r = new Reporte();
        r.setNombreCiudadano(ciudadano);
        r.setTipo(tipo);
        r.setDescripcion(desc);
        r.setLatitud(lat);
        r.setLongitud(lon);
        r.setSectorReferencia(sector);
        r.setGravedad(gravedad);
        r.setEstado(estado);
        r.setFechaCreacion(LocalDateTime.now().minusHours(horasAtras));
        return r;
    }
}
