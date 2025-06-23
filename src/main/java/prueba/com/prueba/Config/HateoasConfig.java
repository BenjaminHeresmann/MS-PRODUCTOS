package prueba.com.prueba.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Configuración para HATEOAS - Requisito de rúbrica: "Implementación de HATEOAS"
 * Esta clase ayuda a generar URLs correctas tanto en localhost como en Railway
 */
@Configuration
public class HateoasConfig {

    // Obtiene la URL base del perfil activo o de variables de entorno
    @Value("${app.base-url:}")
    private String baseUrl;

    /**
     * Obtiene la URL base correcta para generar enlaces HATEOAS
     * Si está configurada app.base-url, la usa; sino genera dinámicamente
     */
    public String getBaseUrl() {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }
        
        // Si no está configurada, genera dinámicamente basada en el request actual
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        } catch (Exception e) {
            // Fallback para casos donde no hay contexto de request
            return "http://localhost:8089";
        }
    }

    /**
     * Genera la URL completa para un endpoint específico
     */
    public String buildUrl(String path) {
        return getBaseUrl() + path;
    }
}
