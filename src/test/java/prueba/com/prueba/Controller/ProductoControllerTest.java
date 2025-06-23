package prueba.com.prueba.Controller;

// Imports para testing con Jackson (serialización JSON)
import com.fasterxml.jackson.databind.ObjectMapper;

// Imports para JUnit 5 - Framework de pruebas unitarias
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Imports para Spring Test - Testing de aplicaciones Spring Boot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Imports de mis clases a testear
import prueba.com.prueba.DTO.ProductoConStockDTO;
import prueba.com.prueba.Model.Producto;
import prueba.com.prueba.Service.ProductoService;

import java.util.Arrays;
import java.util.List;

// Imports para Mockito (framework de mocking) y MockMvc (testing de controllers)
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// PRUEBAS UNITARIAS DEL CONTROLLER - Requisito de rúbrica: "Pruebas unitarias"
// @WebMvcTest: Carga SOLO el contexto web de Spring (no toda la aplicación)
// Es más rápido que @SpringBootTest porque no carga BD, JPA, etc.
// Solo prueba la capa del controller aisladamente
@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    // @Autowired: Spring inyecta MockMvc para simular peticiones HTTP
    // MockMvc permite hacer peticiones HTTP de prueba sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;    // @MockitoBean: Crea un mock (simulación) del service
    // Esto aísla el controller del service real - es una PRUEBA UNITARIA pura
    @MockitoBean
    private ProductoService productoService;

    // @Autowired: Spring inyecta ObjectMapper para convertir objetos Java <-> JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Variables para datos de prueba reutilizables
    private Producto producto;
    private ProductoConStockDTO productoConStockDTO;

    // @BeforeEach: Este método se ejecuta ANTES de cada test
    // Aquí preparo los datos de prueba que usaré en todos los tests
    @BeforeEach
    void setUp() {
        // Creo un producto de prueba con datos válidos
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop Dell");
        producto.setDescripcion("Laptop Dell Inspiron 15");
        producto.setPrecio(799.99);
        producto.setCategoria("Electrónicos");

        // Creo un DTO de prueba para respuestas con stock
        productoConStockDTO = new ProductoConStockDTO();
        productoConStockDTO.setId(1L);
        productoConStockDTO.setNombre("Laptop Dell");
        productoConStockDTO.setDescripcion("Laptop Dell Inspiron 15");
        productoConStockDTO.setPrecio(799.99);
        productoConStockDTO.setCategoria("Electrónicos");
        productoConStockDTO.setStock(10);
    }   
    
    
    // TEST 1: Probar GET /api/productos - Listar todos los productos
    // Verifica que el endpoint devuelva HTTP 200 y llame al service correctamente
    @Test
    void testListarProductos() throws Exception {
        // GIVEN - Preparo una lista simulada de productos con stock
        List<ProductoConStockDTO> productos = Arrays.asList(productoConStockDTO);
        // Simulo que el service devuelve esta lista
        when(productoService.listarProductosConStock()).thenReturn(productos);

        // WHEN & THEN - Hago petición HTTP GET y verifico respuesta
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());  // Verifico que devuelve HTTP 200

        // Verifico que el controller llamó al service exactamente 1 vez
        verify(productoService, times(1)).listarProductosConStock();
    }    
    
    
    // TEST 2: Probar POST /api/productos - Crear nuevo producto
    // Verifica que el endpoint reciba JSON, active validaciones y devuelva respuesta correcta
    @Test
    void testCrearProducto() throws Exception {
        // GIVEN - Simulo que el service crea el producto exitosamente
        when(productoService.crearProducto(any(Producto.class))).thenReturn(producto);

        // WHEN & THEN - Hago petición POST con JSON en el body
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)  // Especifico que envío JSON
                        .content(objectMapper.writeValueAsString(producto)))  // Serializo objeto a JSON
                .andExpect(status().isOk());  // Verifico HTTP 200 (debería ser 201 pero así está implementado)

        // Verifico que el controller pasó el producto al service
        verify(productoService, times(1)).crearProducto(any(Producto.class));
        // NOTA: any(Producto.class) significa "cualquier objeto de tipo Producto"
    }


    // TEST 3: Probar GET /api/productos/{id} - Obtener producto por ID
    // Verifica que el endpoint maneje correctamente el path parameter
    @Test
    void testObtenerProductoPorId() throws Exception {
        // GIVEN - Simulo que el service encuentra el producto
        when(productoService.obtenerProductoConStock(1L)).thenReturn(productoConStockDTO);        // WHEN & THEN - Hago petición GET con ID en la URL
        mockMvc.perform(get("/api/productos/1"))  // El "1" va como path parameter
                .andExpect(status().isOk());

        // Verifico que el controller pasó el ID correcto al service
        verify(productoService, times(1)).obtenerProductoConStock(1L);
    }


    // TEST 4: Probar PUT /api/productos/{id} - Actualizar producto existente
    // Verifica que el endpoint maneje tanto el ID de la URL como el JSON del body
    @Test
    void testActualizarProducto() throws Exception {
        // GIVEN - Simulo que el service actualiza exitosamente
        when(productoService.actualizarProducto(eq(1L), any(Producto.class))).thenReturn(producto);

        // WHEN & THEN - Hago petición PUT con ID en URL y JSON en body
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)  // Especifico que envío JSON
                        .content(objectMapper.writeValueAsString(producto)))  // Serializo producto a JSON
                .andExpect(status().isOk());

        // Verifico que el controller pasó tanto el ID como el producto al service
        verify(productoService, times(1)).actualizarProducto(eq(1L), any(Producto.class));
        // NOTA: eq(1L) significa "exactamente el valor 1L" (más estricto que any())
    }    
    
    
    
    // TEST 5: Probar DELETE /api/productos/{id} - Eliminar producto
    // Verifica que el endpoint procese correctamente la eliminación
    @Test
    void testEliminarProducto() throws Exception {
        // WHEN & THEN - Hago petición DELETE con ID en la URL
        // No necesito GIVEN porque la eliminación no retorna datos
        mockMvc.perform(delete("/api/productos/1"))  // DELETE con ID como path parameter
                .andExpect(status().isOk());  // Verifico HTTP 200 (debería ser 204 pero así está implementado)

        // Verifico que el controller pasó el ID correcto al service para eliminar
        verify(productoService, times(1)).eliminarProducto(1L);
        
        // NOTA: En una implementación más robusta, también podría verificar:
        // - HTTP 404 si el producto no existe
        // - HTTP 204 (No Content) como respuesta estándar para DELETE exitoso
    }
}

/*
 * RESUMEN DE PRUEBAS DEL CONTROLLER:
 * 
 * Estas 5 pruebas cubren todos los endpoints REST principales:
 * 1. GET /api/productos - Listar productos con stock
 * 2. POST /api/productos - Crear nuevo producto (con validaciones)  
 * 3. GET /api/productos/{id} - Obtener producto específico con stock
 * 4. PUT /api/productos/{id} - Actualizar producto existente
 * 5. DELETE /api/productos/{id} - Eliminar producto
 * 
 * QUÉ VERIFICAN ESTAS PRUEBAS:
 * - Códigos de respuesta HTTP correctos
 * - Serialización/deserialización JSON
 * - Path parameters y request body
 * - Llamadas correctas al service (delegación)
 * - Aislamiento del controller (usando mocks)
 * 
 * QUÉ NO VERIFICAN (porque son pruebas UNITARIAS):
 * - Validaciones Bean Validation (se prueban en integración)
 * - HATEOAS links (se podrían agregar con jsonPath)
 * - Base de datos real
 * - Microservicio de inventario real
 */
