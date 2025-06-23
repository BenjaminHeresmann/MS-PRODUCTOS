# FLUJO DETALLADO DE ENDPOINTS - MS-PRODUCTOS
## Explicación técnica para defensa del proyecto

---

## 🔄 FLUJO COMPLETO DE CADA ENDPOINT

### 1. POST /api/productos - Crear Producto

**Flujo paso a paso:**
```
1. Cliente envía JSON → ProductoController.crearProducto()
2. @Valid ejecuta validaciones Bean Validation en Producto
3. Si hay errores → Spring devuelve HTTP 400 + detalles
4. Si OK → Controller llama ProductoService.crearProducto()
5. Service guarda en BD → ProductoRepository.save()
6. Service retorna Producto guardado
7. Controller convierte a ProductoDTO
8. Controller agrega enlaces HATEOAS
9. Respuesta HTTP 201 + ProductoDTO con enlaces
```

**Código clave:**
```java
// 1. Validación automática
public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody Producto producto) {
    
// 2. Lógica de negocio
Producto nuevoProducto = productoService.crearProducto(producto);

// 3. Conversión a DTO
ProductoDTO productoDTO = convertirADTO(nuevoProducto);

// 4. Enlaces HATEOAS
productoDTO.add(linkTo(methodOn(ProductoController.class)
    .obtenerProducto(nuevoProducto.getId())).withSelfRel());

// 5. Respuesta
return ResponseEntity.status(HttpStatus.CREATED).body(productoDTO);
```

---

### 2. GET /api/productos - Listar Productos con Stock

**Flujo paso a paso:**
```
1. Cliente solicita → ProductoController.listarProductosConStock()
2. Controller llama → ProductoService.listarProductosConStock()
3. Service obtiene productos → ProductoRepository.findAll()
4. Para CADA producto:
   a. Service llama → obtenerStockSeguro(productoId)
   b. RestTemplate hace HTTP GET → Microservicio Inventario
   c. Si respuesta OK → usa stock real
   d. Si error → usa stock = 0 (fallback)
5. Service crea ProductoConStockDTO con datos + stock
6. Controller agrega enlaces HATEOAS a cada DTO
7. Respuesta HTTP 200 + Lista de ProductoConStockDTO
```

**Código clave - Integración con microservicio:**
```java
public Integer obtenerStockSeguro(Long productoId) {
    try {
        String url = inventarioServiceUrl + "/inventario/" + productoId;
        InventarioResponse response = restTemplate.getForObject(url, InventarioResponse.class);
        return response != null ? response.getStock() : 0;
    } catch (Exception e) {
        log.warn("Error al obtener stock para producto {}: {}", productoId, e.getMessage());
        return 0; // Patrón Circuit Breaker básico
    }
}
```

---

### 3. GET /api/productos/{id} - Obtener Producto con Stock

**Flujo paso a paso:**
```
1. Cliente solicita → ProductoController.obtenerProductoConStock(id)
2. Controller llama → ProductoService.obtenerProductoConStock(id)
3. Service busca producto → ProductoRepository.findById(id)
4. Si no existe → lanza RuntimeException
5. Si existe → Service obtiene stock del microservicio externo
6. Service crea ProductoConStockDTO
7. Controller agrega enlaces HATEOAS específicos
8. Respuesta HTTP 200 + ProductoConStockDTO
```

**Manejo de errores:**
```java
// En ProductoService
public ProductoConStockDTO obtenerProductoConStock(Long id) {
    Producto producto = productoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    
    Integer stock = obtenerStockSeguro(id);
    return new ProductoConStockDTO(producto.getId(), producto.getNombre(), 
                                  producto.getPrecio(), stock);
}
```

---

### 4. PUT /api/productos/{id} - Actualizar Producto

**Flujo paso a paso:**
```
1. Cliente envía JSON + ID → ProductoController.actualizarProducto()
2. @Valid valida datos del request body
3. Controller llama → ProductoService.actualizarProducto(id, producto)
4. Service busca producto existente por ID
5. Si no existe → lanza excepción
6. Si existe → actualiza campos y guarda
7. Service retorna producto actualizado
8. Controller convierte a DTO + enlaces HATEOAS
9. Respuesta HTTP 200 + ProductoDTO actualizado
```

---

### 5. DELETE /api/productos/{id} - Eliminar Producto

**Flujo paso a paso:**
```
1. Cliente solicita → ProductoController.eliminarProducto(id)
2. Controller llama → ProductoService.eliminarProducto(id)
3. Service verifica existencia del producto
4. Si no existe → lanza excepción
5. Si existe → ProductoRepository.deleteById(id)
6. Controller retorna HTTP 204 (No Content)
```

---

## 🧪 CÓMO FUNCIONAN LAS PRUEBAS UNITARIAS

### Pruebas de Controller (ProductoControllerTest)

**¿Qué testea?**
- Códigos de respuesta HTTP correctos
- Serialización/deserialización JSON
- Validaciones de entrada
- Estructura de respuesta

**Ejemplo técnico:**
```java
@Test
void testCrearProducto() throws Exception {
    // ARRANGE - Preparar datos de prueba
    Producto producto = new Producto();
    producto.setNombre("Test");
    producto.setPrecio(100.0);
    
    ProductoDTO productoDTO = new ProductoDTO();
    // ... configurar DTO
    
    // Mock del service
    when(productoService.crearProducto(any(Producto.class)))
        .thenReturn(producto);
    
    // ACT & ASSERT - Ejecutar petición HTTP y verificar
    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isCreated()) // Verifica HTTP 201
            .andExpect(jsonPath("$.nombre").value("Test")) // Verifica JSON
            .andExpect(jsonPath("$._links.self").exists()); // Verifica HATEOAS
}
```

### Pruebas de Service (ProductoServiceTest)

**¿Qué testea?**
- Lógica de negocio
- Interacción con repository
- Manejo de excepciones
- Integration con servicio externo

**Ejemplo técnico:**
```java
@Test
void testObtenerStockSeguro_ConError() {
    // ARRANGE
    Long productoId = 1L;
    // Simulamos error en el servicio externo
    when(restTemplate.getForObject(anyString(), eq(ProductoService.InventarioResponse.class)))
        .thenThrow(new RuntimeException("Servicio no disponible"));
    
    // ACT
    Integer stock = productoService.obtenerStockSeguro(productoId);
    
    // ASSERT
    assertEquals(0, stock); // Verifica fallback
    verify(restTemplate).getForObject(contains("/inventario/1"), any());
}
```

---

## 🔧 CONFIGURACIÓN POR AMBIENTES

### Desarrollo (application-dev.properties)
```properties
# Logging detallado para debugging
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG  # Ver queries SQL
logging.level.org.hibernate=DEBUG      # Ver operaciones Hibernate
```

**¿Por qué estos logs?**
- `org.springframework.web=DEBUG`: Ver requests/responses HTTP
- `org.hibernate.SQL=DEBUG`: Ver queries SQL generadas
- Ayuda en desarrollo para entender qué hace la aplicación

### Producción (application-prod.properties)
```properties
# Logging optimizado para performance
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
server.port=${PORT:8089}  # Puerto configurable por env var
```

**¿Por qué estos cambios?**
- Menos logs = mejor performance
- Puerto configurable para diferentes plataformas cloud
- Variables de entorno evitan hardcodear valores

---

## 🌐 INTEGRACIÓN CON MICROSERVICIO EXTERNO

### Configuración RestTemplate
```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Uso en Service
```java
@Service
public class ProductoService {
    
    @Value("${inventario.service.url}")  // Inyección desde properties
    private String inventarioServiceUrl;
    
    @Autowired
    private RestTemplate restTemplate;   // Bean inyectado
    
    // Método con patrón Circuit Breaker básico
    public Integer obtenerStockSeguro(Long productoId) {
        try {
            String url = inventarioServiceUrl + "/inventario/" + productoId;
            InventarioResponse response = restTemplate.getForObject(url, InventarioResponse.class);
            return response != null ? response.getStock() : 0;
        } catch (Exception e) {
            // Log del error pero no falla la aplicación
            return 0; // Valor por defecto
        }
    }
}
```

**¿Por qué esta implementación?**
- **Resilencia**: Si el servicio externo falla, nuestra API sigue funcionando
- **Configurabilidad**: URL configurable por ambiente
- **Logging**: Se registran errores para monitoreo
- **Fallback**: Valor por defecto (stock = 0) en caso de error

---

## 📊 MONITOREO CON ACTUATOR

### Endpoints disponibles:
- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información de la aplicación  
- `/actuator/metrics` - Métricas de performance

### Respuesta de health check:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Oracle",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

**¿Para qué sirve?**
- Plataformas cloud usan `/actuator/health` para verificar si la app está funcionando
- Si health check falla, la plataforma puede reiniciar la aplicación
- Métricas ayudan a identificar problemas de performance

---

## 🎯 PUNTOS CLAVE PARA LA DEFENSA

### 1. Arquitectura general:
"Implementé una arquitectura en capas con separación clara de responsabilidades: Controller para endpoints REST, Service para lógica de negocio, Repository para acceso a datos"

### 2. Validaciones:
"Usé Bean Validation con anotaciones como @NotBlank y @Positive, que se ejecutan automáticamente cuando el controller recibe @Valid"

### 3. HATEOAS:
"Los DTOs extienden RepresentationModel para poder agregar enlaces hipermedia, haciendo la API auto-descubrible según los principios REST"

### 4. Integración con microservicio:
"Implementé un patrón Circuit Breaker básico para que si el servicio de inventario falla, nuestra API siga funcionando con un valor por defecto"

### 5. Pruebas:
"Las pruebas de controller usan @WebMvcTest para probar solo la capa web, mientras que las de service usan Mockito para aislar la lógica de negocio"

### 6. Configuración por ambientes:
"Uso Spring Profiles para tener configuraciones diferentes entre desarrollo y producción, especialmente en logging y variables de entorno"
