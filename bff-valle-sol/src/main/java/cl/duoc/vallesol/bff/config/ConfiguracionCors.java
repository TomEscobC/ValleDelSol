package cl.duoc.vallesol.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS para que el frontend Angular (localhost:4200) consuma el BFF.
 */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {

    @Value("${frontend.origen:http://localhost:4200}")
    private String origenFrontend;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(origenFrontend)
            .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
    }
}
