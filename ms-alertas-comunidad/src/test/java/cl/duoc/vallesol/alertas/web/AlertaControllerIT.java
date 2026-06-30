package cl.duoc.vallesol.alertas.web;

import cl.duoc.vallesol.alertas.dto.GenerarAlertaRequest;
import cl.duoc.vallesol.alertas.repository.AlertaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de INTEGRACION del Motor de Alertas (REST + persistencia con H2).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ms-alertas - pruebas de integracion (REST + persistencia)")
class AlertaControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private AlertaRepository repository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
    }

    private GenerarAlertaRequest request(String gravedad) {
        GenerarAlertaRequest r = new GenerarAlertaRequest();
        r.setReporteOrigenId(1L);
        r.setGravedad(gravedad);
        r.setZona("Cruce Los Pinos");
        r.setTipoEmergencia("INCENDIO_FORESTAL");
        return r;
    }

    @Test
    @DisplayName("CP-I06: POST /api/alertas/generar (CRITICA) persiste una alerta de EVACUACION")
    void generarAlerta_persiste() throws Exception {
        String json = objectMapper.writeValueAsString(request("CRITICA"));

        mockMvc.perform(post("/api/alertas/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nivel").value("EVACUACION"))
            .andExpect(jsonPath("$.activa").value(true));

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.countByActivaTrue()).isEqualTo(1);
    }

    @Test
    @DisplayName("CP-I07: GET /api/alertas/activas devuelve solo las alertas activas")
    void listarActivas() throws Exception {
        mockMvc.perform(post("/api/alertas/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("ALTA"))))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/alertas/activas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].nivel").value("PREVENTIVA"));
    }

    @Test
    @DisplayName("CP-I08: POST sin zona responde 400 (validacion) y no persiste")
    void generarSinZona_devuelve400() throws Exception {
        GenerarAlertaRequest invalido = request("CRITICA");
        invalido.setZona("");

        mockMvc.perform(post("/api/alertas/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
            .andExpect(status().isBadRequest());

        assertThat(repository.count()).isZero();
    }
}
