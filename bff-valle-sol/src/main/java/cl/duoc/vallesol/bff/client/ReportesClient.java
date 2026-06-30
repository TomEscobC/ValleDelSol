package cl.duoc.vallesol.bff.client;

import cl.duoc.vallesol.bff.dto.CrearReporteBffRequest;
import cl.duoc.vallesol.bff.dto.ReporteDto;
import cl.duoc.vallesol.bff.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Cliente HTTP hacia el microservicio de Reporte Ciudadano. */
@Component
public class ReportesClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ReportesClient(RestTemplate restTemplate,
                          @Value("${services.reportes.url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ReporteDto crear(CrearReporteBffRequest request) {
        try {
            return restTemplate.postForObject(baseUrl + "/api/reportes", request, ReporteDto.class);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("Microservicio de Reportes no disponible", ex);
        }
    }

    public List<ReporteDto> listar() {
        try {
            ReporteDto[] arr = restTemplate.getForObject(baseUrl + "/api/reportes", ReporteDto[].class);
            return arr == null ? Collections.emptyList() : Arrays.asList(arr);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("Microservicio de Reportes no disponible", ex);
        }
    }

    public List<ReporteDto> ultimos() {
        try {
            ReporteDto[] arr = restTemplate.getForObject(baseUrl + "/api/reportes/ultimos", ReporteDto[].class);
            return arr == null ? Collections.emptyList() : Arrays.asList(arr);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("Microservicio de Reportes no disponible", ex);
        }
    }
}
