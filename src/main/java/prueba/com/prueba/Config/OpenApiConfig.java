package prueba.com.prueba.Config;

// Imports para configurar OpenAPI/Swagger - Requisito de rúbrica: "Documentación OpenAPI/Swagger"
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// @Configuration: Le dice a Spring que esta clase contiene configuración de beans
// Esta clase configura la documentación automática de la API
@Configuration
public class OpenApiConfig {

    // @Bean: Spring registra este método como un bean y lo inyecta donde sea necesario
    // Este bean configura la información general que aparece en Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Productos")  // Título que aparece en Swagger UI
                        .version("1.0.0")          // Versión de la API
                        .description("API REST para la gestión de productos con información de stock integrada")                // Se agregan los servidores para local y producción (Railway)
                // Esto permite seleccionar el servidor desde la UI de Swagger
                .servers(List.of(
                        new Server().url("https://web-production-20275.up.railway.app").description("Servidor de Producción (Railway)"),
                        new Server().url("http://localhost:8089").description("Servidor Local de Desarrollo")
                ));
    }
    
    // NOTA: La documentación detallada de cada endpoint se hace con anotaciones
    // @Operation, @ApiResponse, etc. directamente en los controllers
    // Esta configuración es solo para la información general de la API
}
