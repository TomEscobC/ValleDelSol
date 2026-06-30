package cl.duoc.vallesol.bff.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba END TO END del flujo de negocio critico, atravesando el BFF completo.
 *
 * Simula los microservicios de Reportes y Alertas (MockRestServiceServer) y verifica
 * que una solicitud HTTP real al BFF dispare la orquestacion correcta:
 *   POST /api/bff/reportes (CRITICA) -> llama a ms-reportes -> llama a ms-alertas -> responde alerta.
 *
 * Cubre el "business core": un reporte critico debe terminar generando una alerta de evacuacion.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BFF - prueba end to end del flujo de negocio")
class BffFlujoNegocioE2EIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ObjectMapper objectMapper;

    @Value("${services.reportes.url}") private String reportesUrl;
    @Value("${services.alertas.url}") private String alertasUrl;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    private static final String REPORTE_CRITICO_JSON = """
        {
          "id": 1, "folio": "REP-0001", "nombreCiudadano": "Camila Rojas",
          "tipo": "INCENDIO_FORESTAL", "descripcion": "Foco activo con llamas cerca de viviendas.",
          "latitud": -34.70, "longitud": -71.01, "sectorReferencia": "Curva El Venado",
          "gravedad": "CRITICA", "estado": "PENDIENTE", "fechaCreacion": "2026-06-29T10:00:00"
        }
        """;

    private static final String ALERTA_JSON = """
        {
          "id": 50, "titulo": "EVACUACION INMEDIATA - Curva El Venado",
          "mensaje": "Se ordena evacuacion inmediata del sector Curva El Venado.",
          "nivel": "EVACUACION", "zona": "Curva El Venado", "reporteOrigenId": 1,
          "activa": true, "fechaEmision": "2026-06-29T10:00:01"
        }
        """;

    private static final String REPORTE_MEDIO_JSON = """
        {
          "id": 2, "folio": "REP-0002", "nombreCiudadano": "Valentina Soto",
          "tipo": "QUEMA_NO_AUTORIZADA", "descripcion": "Quema de ramas sin autorizacion.",
          "latitud": -34.69, "longitud": -71.04, "sectorReferencia": "Camino El Roble",
          "gravedad": "MEDIA", "estado": "PENDIENTE", "fechaCreacion": "2026-06-29T11:00:00"
        }
        """;

    private String bffRequest(String gravedad, String tipo, String desc) throws Exception {
        return """
            {
              "nombreCiudadano": "Camila Rojas", "tipo": "%s",
              "descripcion": "%s",
              "latitud": -34.70, "longitud": -71.01,
              "sectorReferencia": "Curva El Venado", "gravedad": "%s"
            }
            """.formatted(tipo, desc, gravedad);
    }

    @Test
    @DisplayName("CP-E2E01: reporte CRITICO -> el BFF persiste el reporte Y genera la alerta de evacuacion")
    void flujoCritico_generaAlerta() throws Exception {
        mockServer.expect(requestTo(reportesUrl + "/api/reportes"))
            .andExpect(method(org.springframework.http.HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON).body(REPORTE_CRITICO_JSON));

        mockServer.expect(requestTo(alertasUrl + "/api/alertas/generar"))
            .andExpect(method(org.springframework.http.HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON).body(ALERTA_JSON));

        mockMvc.perform(post("/api/bff/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bffRequest("CRITICA", "INCENDIO_FORESTAL", "Foco activo con llamas cerca de viviendas.")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.reporte.folio").value("REP-0001"))
            .andExpect(jsonPath("$.alertaGenerada").value(true))
            .andExpect(jsonPath("$.alerta.nivel").value("EVACUACION"));

        mockServer.verify();
    }

    @Test
    @DisplayName("CP-E2E02: reporte MEDIA -> el BFF persiste el reporte y NO llama al motor de alertas")
    void flujoNoCritico_noGeneraAlerta() throws Exception {
        // Solo se espera la llamada a Reportes. Si el BFF llamara a Alertas, la prueba fallaria.
        mockServer.expect(requestTo(reportesUrl + "/api/reportes"))
            .andExpect(method(org.springframework.http.HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON).body(REPORTE_MEDIO_JSON));

        mockMvc.perform(post("/api/bff/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bffRequest("MEDIA", "QUEMA_NO_AUTORIZADA", "Quema de ramas sin autorizacion en el sector.")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.alertaGenerada").value(false));

        mockServer.verify();
    }
}
