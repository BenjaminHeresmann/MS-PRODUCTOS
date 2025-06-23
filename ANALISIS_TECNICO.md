# ANÁLISIS TÉCNICO DEL PROYECTO - MS-PRODUCTOS
## Implementación de Microservicio Spring Boot según Rúbrica Académica

---

## 📋 REQUISITOS CUMPLIDOS DE LA RÚBRICA

### ✅ 1. VALIDACIONES MÍNIMAS
**¿Qué implementé?**
- Agregué Bean Validation en el modelo `Producto.java`
- Usé anotaciones `@NotBlank`, `@NotNull`, `@Positive`
- Implementé `@Valid` en el controller para activar las validaciones

**¿Cómo funciona técnicamente?**
```java
// En Producto.java
@NotBlank(message = "El nombre es obligatorio")
private String nombre;

@NotNull(message = "El precio es obligatorio") 
@Positive(message = "El precio debe ser positivo")
private Double precio;

// En ProductoController.java  
public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody Producto producto)
```

**¿Por qué esta implementación?**
- Spring Boot valida automáticamente cuando encuentra `@Valid`
- Si hay errores, Spring devuelve HTTP 400 con detalles del error
- Bean Validation es el estándar JEE para validaciones
- Las validaciones se ejecutan ANTES de llegar a la lógica de negocio

---

### ✅ 2. HATEOAS (Hypermedia as the Engine of Application State)
**¿Qué implementé?**
- Agregué dependencia `spring-boot-starter-hateoas`
- Creé DTOs que extienden `RepresentationModel<>`
- Implementé enlaces navegables en todas las respuestas

**¿Cómo funciona técnicamente?**
```java
// ProductoDTO extiende RepresentationModel para soportar enlaces
public class ProductoDTO extends RepresentationModel<ProductoDTO> {
    // campos del DTO
}

// En el controller genero enlaces dinámicos
productoDTO.add(linkTo(methodOn(ProductoController.class)
    .obtenerProducto(producto.getId())).withSelfRel());
productoDTO.add(linkTo(ProductoController.class).withRel("productos"));
```

**¿Por qué esta implementación?**
- HATEOAS hace la API auto-descubrible
- El cliente recibe URLs para navegar sin hardcodear endpoints
- Facilita la evolución de la API sin romper clientes
- Es un principio REST nivel 3 (Richardson Maturity Model)

**Ejemplo de respuesta HATEOAS:**
```json
{
  "id": 1,
  "nombre": "Producto Test",
  "precio": 100.0,
  "_links": {
    "self": {"href": "http://localhost:8089/api/productos/1"},
    "productos": {"href": "http://localhost:8089/api/productos"}
  }
}
```

---

### ✅ 3. DOCUMENTACIÓN OPENAPI/SWAGGER
**¿Qué implementé?**
- Agregué dependencia `springdoc-openapi-starter-webmvc-ui`
- Creé configuración personalizada en `OpenApiConfig.java`
- Agregué anotaciones de documentación en controllers y modelos

**¿Cómo funciona técnicamente?**
```java
// OpenApiConfig.java - Configuración global de la documentación
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("MS-PRODUCTOS API")
            .version("1.0")
            .description("Microservicio para gestión de productos"));
}

// En ProductoController.java - Documentación de endpoints
@Operation(summary = "Crear nuevo producto", 
           description = "Crea un nuevo producto con validaciones")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
})
```

**¿Por qué esta implementación?**
- OpenAPI 3.0 es el estándar para documentación de APIs REST
- Swagger UI proporciona interfaz interactiva para probar endpoints
- La documentación se genera automáticamente desde el código
- Facilita la integración con otros equipos/sistemas

**URLs de acceso:**
- Swagger UI: `http://localhost:8089/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8089/api-docs`

---

### ✅ 4. PRUEBAS UNITARIAS
**¿Qué implementé?**
- Pruebas para `ProductoController` usando `@WebMvcTest`
- Pruebas para `ProductoService` usando `@ExtendWith(MockitoExtension.class)`
- Cobertura de los 5 endpoints principales
- Mocks para dependencias externas

**¿Cómo funciona técnicamente?**
```java
// ProductoControllerTest.java
@WebMvcTest(ProductoController.class) // Solo carga el contexto web
class ProductoControllerTest {
    
    @MockBean
    private ProductoService productoService; // Mock del servicio
    
    @Test
    void testCrearProducto() throws Exception {
        // Given - Preparar datos de prueba
        Producto producto = new Producto();
        when(productoService.crearProducto(any())).thenReturn(producto);
        
        // When & Then - Ejecutar y verificar
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated());
    }
}
```

**¿Por qué esta implementación?**
- `@WebMvcTest` es más rápido que `@SpringBootTest` (solo contexto web)
- Mockito permite aislar la unidad bajo prueba
- Pruebas unitarias validan lógica específica sin dependencias externas
- Patrón AAA (Arrange, Act, Assert) para claridad

**Tipos de pruebas implementadas:**
- **Controller**: Validación de endpoints, códigos HTTP, serialización JSON
- **Service**: Lógica de negocio, manejo de excepciones, integración con repositorio

---

### ✅ 5. PERFILES DE CONFIGURACIÓN
**¿Qué implementé?**
- `application.properties` - Configuración base
- `application-dev.properties` - Configuración desarrollo
- `application-prod.properties` - Configuración producción
- Variables de entorno para configuración externa

**¿Cómo funciona técnicamente?**
```properties
# application.properties (base)
spring.profiles.active=dev
spring.datasource.url=${DATABASE_URL:valor_por_defecto}

# application-dev.properties (desarrollo)
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# application-prod.properties (producción)  
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
server.port=${PORT:8089}
```

**¿Por qué esta implementación?**
- Permite diferentes configuraciones sin cambiar código
- Variables de entorno facilitan despliegue en diferentes ambientes
- Logging detallado en desarrollo, optimizado en producción
- Principio de configuración externa (12-factor app)

**Activación de perfiles:**
```bash
# Desarrollo
java -jar app.jar --spring.profiles.active=dev

# Producción  
java -jar app.jar --spring.profiles.active=prod
```

---

### ✅ 6. PREPARACIÓN PARA DESPLIEGUE EN LA NUBE
**¿Qué implementé?**
- **Spring Boot Actuator** para health checks
- **Archivos de configuración** para diferentes plataformas
- **Variables de entorno** para configuración externa
- **Containerización opcional** con Docker

**¿Cómo funciona técnicamente?**

**Spring Boot Actuator:**
```properties
# Expone endpoints de monitoreo
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

**Railway (railway.json):**
```json
{
  "deploy": {
    "startCommand": "java -jar target/prueba-0.0.1-SNAPSHOT.jar",
    "healthcheckPath": "/actuator/health"
  }
}
```

**Heroku (Procfile):**
```
web: java -jar target/prueba-0.0.1-SNAPSHOT.jar
```

**¿Por qué esta implementación?**
- Health checks permiten a la plataforma verificar estado de la aplicación
- Configuración externa permite despliegue sin cambios de código
- Múltiples opciones de despliegue (Railway, Heroku, Docker)
- Actuator proporciona métricas para monitoreo en producción

---

## 🏗️ ARQUITECTURA IMPLEMENTADA

### Patrón MVC + Capas
```
┌─────────────────┐
│   Controller    │ ← Capa de presentación (REST endpoints)
├─────────────────┤
│     Service     │ ← Lógica de negocio
├─────────────────┤  
│   Repository    │ ← Acceso a datos (JPA)
├─────────────────┤
│     Model       │ ← Entidades de dominio
└─────────────────┘
```

### Flujo de una petición:
1. **Cliente** → HTTP Request → **Controller**
2. **Controller** → Validación (`@Valid`) → **Service**  
3. **Service** → Lógica de negocio → **Repository**
4. **Repository** → JPA/Hibernate → **Base de datos**
5. **Respuesta** ← HATEOAS + JSON ← **Controller**

### Integración con microservicio externo:
```java
// ProductoService.java
@Value("${inventario.service.url}")
private String inventarioServiceUrl;

public Integer obtenerStockSeguro(Long productoId) {
    try {
        String url = inventarioServiceUrl + "/inventario/" + productoId;
        InventarioResponse response = restTemplate.getForObject(url, InventarioResponse.class);
        return response != null ? response.getStock() : 0;
    } catch (Exception e) {
        return 0; // Fallback en caso de error
    }
}
```

---

## 🔧 TECNOLOGÍAS Y DEPENDENCIAS UTILIZADAS

### Core Framework:
- **Spring Boot 3.4.5** - Framework principal
- **Java 17** - Versión LTS de Java

### Dependencias principales:
- **spring-boot-starter-web** - REST endpoints
- **spring-boot-starter-data-jpa** - Persistencia 
- **spring-boot-starter-validation** - Bean Validation
- **spring-boot-starter-hateoas** - Enlaces hipermedia
- **spring-boot-starter-actuator** - Monitoreo
- **springdoc-openapi** - Documentación API

### Testing:
- **JUnit 5** - Framework de pruebas
- **Mockito** - Mocking framework
- **Spring Boot Test** - Testing Spring Boot

### Base de datos:
- **Oracle JDBC** - Driver de conexión
- **Hibernate** - ORM (Object-Relational Mapping)

---

## 💡 DECISIONES TÉCNICAS TOMADAS

### 1. ¿Por qué DTOs separados?
- **ProductoDTO**: Para respuestas simples
- **ProductoConStockDTO**: Para respuestas con información de inventario
- Evita exponer entidades JPA directamente
- Permite evolución independiente de API y modelo de datos

### 2. ¿Por qué RestTemplate y no WebClient?
- RestTemplate es más simple para este caso de uso
- WebClient sería mejor para aplicaciones reactivas
- Para el alcance del proyecto, RestTemplate es suficiente

### 3. ¿Por qué manejo de errores defensivo?
```java
public Integer obtenerStockSeguro(Long productoId) {
    try {
        // llamada al servicio externo
    } catch (Exception e) {
        return 0; // Fallback
    }
}
```
- Evita que fallos del servicio externo rompan nuestra API
- Proporciona experiencia de usuario consistente
- Implementa patrón Circuit Breaker básico

### 4. ¿Por qué múltiples opciones de despliegue?
- **Flexibilidad**: Diferentes plataformas para diferentes necesidades
- **Aprendizaje**: Conocer múltiples opciones es valioso
- **Robustez**: No depender de una sola plataforma

---

## 🎯 CUMPLIMIENTO DE LA RÚBRICA

| Requisito | Implementación | Archivos clave |
|-----------|---------------|----------------|
| Validaciones | Bean Validation + `@Valid` | `Producto.java`, `ProductoController.java` |
| HATEOAS | RepresentationModel + enlaces | `ProductoDTO.java`, `ProductoConStockDTO.java` |
| OpenAPI/Swagger | springdoc + anotaciones | `OpenApiConfig.java`, controllers |
| Pruebas unitarias | JUnit 5 + Mockito | `ProductoControllerTest.java`, `ProductoServiceTest.java` |
| Perfiles | application-{profile}.properties | `application-*.properties` |
| Despliegue nube | Actuator + configs plataforma | `railway.json`, `Procfile`, `system.properties` |

**RESULTADO: Todos los requisitos implementados correctamente** ✅
