package cl.duoc.vallesol.alertas.service;

import cl.duoc.vallesol.alertas.domain.Alerta;
import cl.duoc.vallesol.alertas.domain.NivelAlerta;
import cl.duoc.vallesol.alertas.dto.GenerarAlertaRequest;
import cl.duoc.vallesol.alertas.exception.AlertaInvalidaException;
import cl.duoc.vallesol.alertas.repository.AlertaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas UNITARIAS del Motor de Alertas. El repositorio se simula con Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaService - reglas de negocio (pruebas unitarias)")
class AlertaServiceTest {

    @Mock
    private AlertaRepository repository;

    @InjectMocks
    private AlertaService service;

    private GenerarAlertaRequest request(String gravedad) {
        GenerarAlertaRequest r = new GenerarAlertaRequest();
        r.setReporteOrigenId(10L);
        r.setGravedad(gravedad);
        r.setZona("Curva El Venado");
        r.setTipoEmergencia("INCENDIO_FORESTAL");
        return r;
    }

    @Test
    @DisplayName("CP-U09: gravedad CRITICA -> nivel EVACUACION")
    void mapearNivel_critica_evacuacion() {
        assertThat(service.mapearNivel("CRITICA")).isEqualTo(NivelAlerta.EVACUACION);
    }

    @Test
    @DisplayName("CP-U10: gravedad ALTA -> nivel PREVENTIVA")
    void mapearNivel_alta_preventiva() {
        assertThat(service.mapearNivel("ALTA")).isEqualTo(NivelAlerta.PREVENTIVA);
    }

    @Test
    @DisplayName("CP-U11: gravedad MEDIA/BAJA -> nivel INFORMATIVA")
    void mapearNivel_otras_informativa() {
        assertThat(service.mapearNivel("MEDIA")).isEqualTo(NivelAlerta.INFORMATIVA);
        assertThat(service.mapearNivel("BAJA")).isEqualTo(NivelAlerta.INFORMATIVA);
    }

    @Test
    @DisplayName("CP-U12: gravedad nula al mapear lanza AlertaInvalidaException")
    void mapearNivel_nula_lanzaExcepcion() {
        assertThatThrownBy(() -> service.mapearNivel(null))
            .isInstanceOf(AlertaInvalidaException.class);
    }

    @Test
    @DisplayName("CP-U13: generar desde reporte critico crea alerta ACTIVA de EVACUACION con trazabilidad")
    void generarDesdeReporte_critico_creaEvacuacionActiva() {
        when(repository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        Alerta alerta = service.generarDesdeReporte(request("CRITICA"));

        assertThat(alerta.getNivel()).isEqualTo(NivelAlerta.EVACUACION);
        assertThat(alerta.isActiva()).isTrue();
        assertThat(alerta.getReporteOrigenId()).isEqualTo(10L);
        assertThat(alerta.getFechaEmision()).isNotNull();
        assertThat(alerta.getTitulo()).contains("EVACUACION");
        assertThat(alerta.getMensaje()).contains("Curva El Venado");
        verify(repository).save(any(Alerta.class));
    }

    @Test
    @DisplayName("CP-U14: generar sin zona lanza AlertaInvalidaException y no persiste")
    void generarDesdeReporte_sinZona_lanzaExcepcion() {
        GenerarAlertaRequest r = request("ALTA");
        r.setZona("  ");

        assertThatThrownBy(() -> service.generarDesdeReporte(r))
            .isInstanceOf(AlertaInvalidaException.class);

        verify(repository, never()).save(any());
    }
}
