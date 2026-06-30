package cl.duoc.vallesol.reportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Reporte Ciudadano.
 *
 * Caso Valle del Sol - Subdireccion de Gestion de Emergencias.
 * Permite a la comunidad reportar focos de incendio con geolocalizacion,
 * persistiendo cada reporte y exponiendolo via API REST para que el BFF
 * y el Centro de Comando lo consuman.
 */
@SpringBootApplication
public class ReportesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportesApplication.class, args);
    }
}
