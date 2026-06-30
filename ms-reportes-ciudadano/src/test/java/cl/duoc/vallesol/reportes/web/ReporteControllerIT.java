package cl.duoc.vallesol.reportes.web;

import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.TipoEmergencia;
import cl.duoc.vallesol.reportes.dto.CrearReporteRequest;
import cl.duoc.vallesol.reportes.repository.ReporteRepository;
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
 * Pruebas de INTEGRACION del microservicio de Reportes.
 * Levantan el contexto completo de Spring + JPA contra una base H2 en memoria,
 * verificando el camino REST -> Servicio -> Repositorio -> BD (persistencia real).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ms-reportes - pruebas de integracion (REST + persistencia)")
class ReporteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReporteRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
    }

    private CrearReporteRequest requestValido() {
        CrearReporteRequest r = new CrearReporteRequest();
        r.setNombreCiudadano("Jorge Munoz");
        r.setTipo(TipoEmergencia.INCENDIO_FORESTAL);
        r.setDescripcion("Foco activo avanzando hacia viviendas en el sector Los Pinos.");
        r.setLatitud(-34.71);
        r.setLongitud(-71.03);
        r.setSectorReferencia("Cruce Los Pinos");
        r.setGravedad(Gravedad.ALTA);
        return r;
    }

    @Test
    @DisplayName("CP-I01: POST /api/reportes crea y PERSISTE el reporte (201 + registro en BD)")
    void crearReporte_persisteEnBaseDeDatos() throws Exception {
        String json = objectMapper.writeValueAsString(requestValido());

        mockMvc.perform(post("/api/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
            .andExpect(jsonPath("$.gravedad").value("ALTA"));

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("CP-I02: POST con coordenadas fuera de rango responde 400 y NO persiste")
    void crearReporte_coordenadasInvalidas_devuelve400() throws Exception {
        CrearReporteRequest invalido = requestValido();
        invalido.setLatitud(200.0);
        String json = objectMapper.writeValueAsString(invalido);

        mockMvc.perform(post("/api/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());

        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("CP-I03: POST con descripcion vacia responde 400 (validacion de entrada)")
    void crearReporte_descripcionVacia_devuelve400() throws Exception {
        CrearReporteRequest invalido = requestValido();
        invalido.setDescripcion("");
        String json = objectMapper.writeValueAsString(invalido);

        mockMvc.perform(post("/api/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CP-I04: flujo completo crear -> consultar por id devuelve el mismo reporte")
    void crearYConsultarPorId() throws Exception {
        String json = objectMapper.writeValueAsString(requestValido());

        String respuesta = mockMvc.perform(post("/api/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(respuesta).get("id").asLong();

        mockMvc.perform(get("/api/reportes/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreCiudadano").value("Jorge Munoz"))
            .andExpect(jsonPath("$.folio").value(String.format("REP-%04d", id)));
    }

    @Test
    @DisplayName("CP-I05: GET /api/reportes/{id} inexistente responde 404")
    void consultarInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/reportes/{id}", 9999))
            .andExpect(status().isNotFound());
    }
}
