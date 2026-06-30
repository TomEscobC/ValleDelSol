package cl.duoc.vallesol.alertas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio Motor de Alertas a la Comunidad.
 *
 * Caso Valle del Sol. Genera y administra las alertas masivas (informativas,
 * preventivas y de evacuacion) que se difunden a la comunidad cuando un reporte
 * ciudadano es critico. Persiste cada alerta para trazabilidad historica.
 */
@SpringBootApplication
public class AlertasApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlertasApplication.class, args);
    }
}
