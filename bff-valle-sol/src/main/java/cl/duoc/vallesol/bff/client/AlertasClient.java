package cl.duoc.vallesol.bff.client;

import cl.duoc.vallesol.bff.dto.AlertaDto;
import cl.duoc.vallesol.bff.dto.GenerarAlertaBffRequest;
import cl.duoc.vallesol.bff.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Cliente HTTP hacia el microservicio Motor de Alertas a la Comunidad. */
@Component
public class AlertasClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AlertasClient(RestTemplate restTemplate,
                         @Value("${services.alertas.url:http://localhost:8082}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public AlertaDto generar(GenerarAlertaBffRequest request) {
        try {
            return restTemplate.postForObject(baseUrl + "/api/alertas/generar", request, AlertaDto.class);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("Microservicio de Alertas no disponible", ex);
        }
    }

    public List<AlertaDto> listarActivas() {
        try {
            AlertaDto[] arr = restTemplate.getForObject(baseUrl + "/api/alertas/activas", AlertaDto[].class);
            return arr == null ? Collections.emptyList() : Arrays.asList(arr);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("Microservicio de Alertas no disponible", ex);
        }
    }
}
