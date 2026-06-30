package cl.duoc.vallesol.reportes.service;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.Reporte;
import cl.duoc.vallesol.reportes.domain.TipoEmergencia;
import cl.duoc.vallesol.reportes.dto.CrearReporteRequest;
import cl.duoc.vallesol.reportes.exception.RecursoNoEncontradoException;
import cl.duoc.vallesol.reportes.exception.ReporteInvalidoException;
import cl.duoc.vallesol.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas UNITARIAS de las reglas de negocio del Reporte Ciudadano.
 * No levantan contexto de Spring ni base de datos: el repositorio se simula con Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteService - reglas de negocio (pruebas unitarias)")
class ReporteServiceTest {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService service;

    private CrearReporteRequest requestValido() {
        CrearReporteRequest r = new CrearReporteRequest();
        r.setNombreCiudadano("Camila Rojas");
        r.setTipo(TipoEmergencia.INCENDIO_FORESTAL);
        r.setDescripcion("Foco activo con llamas cerca de viviendas en la ruta 5.");
        r.setLatitud(-34.70);
        r.setLongitud(-71.01);
        r.setSectorReferencia("Curva El Venado");
        r.setGravedad(Gravedad.CRITICA);
        return r;
    }

    @Test
    @DisplayName("CP-U01: un reporte ALTA o CRITICA es critico; BAJA/MEDIA no lo son")
    void esCritico_segunGravedad() {
        Reporte r = new Reporte();
        r.setGravedad(Gravedad.CRITICA);
        assertThat(service.esCritico(r)).isTrue();
        r.setGravedad(Gravedad.ALTA);
        assertThat(service.esCritico(r)).isTrue();
        r.setGravedad(Gravedad.MEDIA);
        assertThat(service.esCritico(r)).isFalse();
        r.setGravedad(Gravedad.BAJA);
        assertThat(service.esCritico(r)).isFalse();
    }

    @Test
    @DisplayName("CP-U02: al crear, el reporte nace PENDIENTE y con fecha de creacion")
    void crear_estableceEstadoInicialYFecha() {
        when(repository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Reporte creado = service.crear(requestValido());

        assertThat(creado.getEstado()).isEqualTo(EstadoReporte.PENDIENTE);
        assertThat(creado.getFechaCreacion()).isNotNull();
        verify(repository, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("CP-U03: crear con latitud fuera de rango (200) lanza ReporteInvalidoException [BUG-01]")
    void crear_latitudFueraDeRango_lanzaExcepcion() {
        CrearReporteRequest r = requestValido();
        r.setLatitud(200.0); // fuera de [-90, 90]

        assertThatThrownBy(() -> service.crear(r))
            .isInstanceOf(ReporteInvalidoException.class)
            .hasMessageContaining("Latitud fuera de rango");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("CP-U04: crear con longitud fuera de rango lanza ReporteInvalidoException")
    void crear_longitudFueraDeRango_lanzaExcepcion() {
        CrearReporteRequest r = requestValido();
        r.setLongitud(-500.0);

        assertThatThrownBy(() -> service.crear(r))
            .isInstanceOf(ReporteInvalidoException.class)
            .hasMessageContaining("Longitud fuera de rango");
    }

    @Test
    @DisplayName("CP-U05: crear con descripcion demasiado corta lanza ReporteInvalidoException")
    void crear_descripcionCorta_lanzaExcepcion() {
        CrearReporteRequest r = requestValido();
        r.setDescripcion("humo"); // < 10 caracteres

        assertThatThrownBy(() -> service.crear(r))
            .isInstanceOf(ReporteInvalidoException.class)
            .hasMessageContaining("descripcion");
    }

    @Test
    @DisplayName("CP-U06: cambiar estado de un reporte inexistente lanza RecursoNoEncontradoException")
    void cambiarEstado_inexistente_lanzaNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cambiarEstado(99L, EstadoReporte.VALIDADO))
            .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("CP-U07: transicion invalida (RESUELTO -> PENDIENTE) lanza ReporteInvalidoException")
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        Reporte r = new Reporte();
        r.setId(5L);
        r.setEstado(EstadoReporte.RESUELTO);
        when(repository.findById(5L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.cambiarEstado(5L, EstadoReporte.PENDIENTE))
            .isInstanceOf(ReporteInvalidoException.class)
            .hasMessageContaining("Transicion invalida");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("CP-U08: transicion valida (PENDIENTE -> VALIDADO) actualiza y persiste")
    void cambiarEstado_transicionValida_actualiza() {
        Reporte r = new Reporte();
        r.setId(7L);
        r.setEstado(EstadoReporte.PENDIENTE);
        when(repository.findById(7L)).thenReturn(Optional.of(r));
        when(repository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Reporte actualizado = service.cambiarEstado(7L, EstadoReporte.VALIDADO);

        assertThat(actualizado.getEstado()).isEqualTo(EstadoReporte.VALIDADO);
        assertThat(actualizado.getFechaActualizacion()).isNotNull();
        verify(repository).save(r);
    }
}
