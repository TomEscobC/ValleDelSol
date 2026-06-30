package cl.duoc.vallesol.reportes.service;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.Reporte;
import cl.duoc.vallesol.reportes.dto.CrearReporteRequest;
import cl.duoc.vallesol.reportes.exception.RecursoNoEncontradoException;
import cl.duoc.vallesol.reportes.exception.ReporteInvalidoException;
import cl.duoc.vallesol.reportes.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica de negocio del Reporte Ciudadano.
 *
 * Concentra las reglas criticas del negocio que son objeto de las pruebas unitarias:
 *  - Validacion de datos del reporte (coordenadas, descripcion).
 *  - Estado inicial y marca temporal.
 *  - Maquina de estados (transiciones permitidas).
 *  - Determinacion de criticidad (dispara alerta comunitaria).
 */
@Service
public class ReporteService {

    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Reporte crear(CrearReporteRequest request) {
        validarDatosNegocio(request);

        Reporte reporte = new Reporte();
        reporte.setNombreCiudadano(request.getNombreCiudadano().trim());
        reporte.setTipo(request.getTipo());
        reporte.setDescripcion(request.getDescripcion().trim());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        reporte.setSectorReferencia(request.getSectorReferencia());
        reporte.setGravedad(request.getGravedad());
        // Regla: todo reporte nace PENDIENTE de validacion por el Centro de Comando.
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setFechaCreacion(LocalDateTime.now());

        return repository.save(reporte);
    }

    @Transactional(readOnly = true)
    public List<Reporte> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reporte> listarPorEstado(EstadoReporte estado) {
        return repository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Reporte> ultimosReportes() {
        return repository.findTop10ByOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public Reporte obtener(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe el reporte con id " + id));
    }

    @Transactional(readOnly = true)
    public long contarPorGravedad(Gravedad gravedad) {
        return repository.countByGravedad(gravedad);
    }

    /**
     * Cambia el estado de un reporte respetando la maquina de estados del negocio.
     * @throws RecursoNoEncontradoException si el reporte no existe.
     * @throws ReporteInvalidoException     si la transicion no esta permitida.
     */
    @Transactional
    public Reporte cambiarEstado(Long id, EstadoReporte nuevoEstado) {
        Reporte reporte = obtener(id);
        if (nuevoEstado == null) {
            throw new ReporteInvalidoException("El nuevo estado no puede ser nulo");
        }
        if (!reporte.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new ReporteInvalidoException(
                "Transicion invalida: no se puede pasar de " + reporte.getEstado()
                    + " a " + nuevoEstado);
        }
        reporte.setEstado(nuevoEstado);
        reporte.setFechaActualizacion(LocalDateTime.now());
        return repository.save(reporte);
    }

    /** Regla de negocio: un reporte critico (ALTA/CRITICA) debe disparar alerta comunitaria. */
    public boolean esCritico(Reporte reporte) {
        return reporte != null
            && reporte.getGravedad() != null
            && reporte.getGravedad().esCritica();
    }

    /**
     * Reglas de negocio aplicadas en la creacion, mas alla del formato.
     * Defensa en profundidad: aunque el controlador valide el DTO, el servicio
     * vuelve a validar para proteger cualquier otro origen de datos.
     */
    private void validarDatosNegocio(CrearReporteRequest request) {
        if (request == null) {
            throw new ReporteInvalidoException("El reporte no puede ser nulo");
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().length() < 10) {
            throw new ReporteInvalidoException(
                "La descripcion debe tener al menos 10 caracteres para ser util a las brigadas");
        }
        if (request.getGravedad() == null) {
            throw new ReporteInvalidoException("La gravedad es obligatoria");
        }
        validarCoordenadas(request.getLatitud(), request.getLongitud());
    }

    /**
     * Valida que las coordenadas esten dentro de rangos geograficos validos.
     * Esta validacion se agrego luego de que las pruebas detectaran que
     * coordenadas fuera de rango (ej. lat=200) eran aceptadas (ver Informe de Pruebas, BUG-01).
     */
    public void validarCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new ReporteInvalidoException("Las coordenadas son obligatorias");
        }
        if (latitud < -90.0 || latitud > 90.0) {
            throw new ReporteInvalidoException("Latitud fuera de rango [-90, 90]: " + latitud);
        }
        if (longitud < -180.0 || longitud > 180.0) {
            throw new ReporteInvalidoException("Longitud fuera de rango [-180, 180]: " + longitud);
        }
    }
}
