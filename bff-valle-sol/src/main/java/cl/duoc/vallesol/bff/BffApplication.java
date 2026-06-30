package cl.duoc.vallesol.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Backend For Frontend (BFF) de Valle del Sol.
 *
 * Es el unico punto de entrada del frontend Angular. Orquesta el flujo de negocio
 * (registrar reporte -> generar alerta si es critico) y agrega informacion de los
 * microservicios de Reportes y Alertas para el Dashboard del Centro de Comando.
 */
@SpringBootApplication
public class BffApplication {
    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}
