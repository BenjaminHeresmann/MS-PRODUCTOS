package prueba.com.prueba.Service;

// Imports para JUnit 5 - Framework de pruebas moderno
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Imports para Mockito - Framework de mocking
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Import de Spring para cliente REST
import org.springframework.web.client.RestTemplate;

// Imports de mis clases del proyecto
import prueba.com.prueba.DTO.ProductoConStockDTO;
import prueba.com.prueba.Model.Producto;
import prueba.com.prueba.Repository.ProductoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Imports para assertions y mocking
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// PRUEBAS UNITARIAS DEL SERVICE - Requisito de rúbrica: "Pruebas unitarias"
// @ExtendWith(MockitoExtension.class): Activa Mockito para crear mocks automáticamente
// Estas pruebas AÍSLAN la lógica del service de sus dependencias (repository, restTemplate)
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    // @Mock: Crea un mock (simulación) del repository
    // No se conecta a BD real - es una PRUEBA UNITARIA pura
    @Mock
    private ProductoRepository productoRepository;

    // @Mock: Crea un mock del RestTemplate
    // No hace llamadas HTTP reales - simulo las respuestas
    @Mock
    private RestTemplate restTemplate;

    // @InjectMocks: Mockito inyecta automáticamente los mocks en ProductoService
    // Crea una instancia real del service pero con dependencias simuladas
    @InjectMocks
    private ProductoService productoService;

    // Variable para datos de prueba reutilizables
    private Producto producto;

    // @BeforeEach: Se ejecuta antes de cada test individual
    // Preparo datos de prueba consistentes para todos los tests
    @BeforeEach
    void setUp() {
        // Creo un producto de prueba con datos válidos y realistas
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop Dell");
        producto.setDescripcion("Laptop Dell Inspiron 15");
        producto.setPrecio(799.99);
        producto.setCategoria("Electrónicos");
    }    
    
    
    
    
    // TEST 1: Probar el método básico listarProductos()
    // Verifica que el service delegue correctamente al repository
    @Test
    void testListarProductos() {
        // GIVEN - Preparar el escenario de prueba
        // Simulo que el repository devuelve una lista con un producto
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        // WHEN - Ejecutar el método bajo prueba
        List<Producto> resultado = productoService.listarProductos();

        // THEN - Verificar los resultados
        // Verifico que devuelve datos correctos
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laptop Dell", resultado.get(0).getNombre());
        // Verifico que se llamó al repository exactamente 1 vez
        verify(productoRepository, times(1)).findAll();
    }


    

    // TEST 2: Probar el método crearProducto()
    // Verifica que el service guarde correctamente usando el repository
    @Test
    void testCrearProducto() {
        // GIVEN - El repository simulará que guarda exitosamente
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // WHEN - Ejecuto el método de crear
        Producto resultado = productoService.crearProducto(producto);

        // THEN - Verifico que el resultado sea correcto
        assertNotNull(resultado);
        assertEquals("Laptop Dell", resultado.getNombre());
        assertEquals(799.99, resultado.getPrecio());
        // Verifico que se llamó a save() con cualquier producto
        verify(productoRepository, times(1)).save(any(Producto.class));
    }



    // TEST 3: Probar obtenerProductoPorId() - caso exitoso
    // Verifica que funcione cuando el producto existe
    @Test
    void testObtenerProductoPorId() {
        // GIVEN - Simulo que el producto existe en la BD
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // WHEN - Busco el producto por ID
        Producto resultado = productoService.obtenerProductoPorId(1L);

        // THEN - Verifico que lo encontró correctamente
        assertNotNull(resultado);
        assertEquals("Laptop Dell", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }



    // TEST 4: Probar obtenerProductoPorId() - caso de producto no existente
    // Verifica el manejo cuando el producto no existe
    @Test
    void testObtenerProductoPorIdNoExistente() {
        // GIVEN - Simulo que el producto NO existe
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN - Busco un producto inexistente
        Producto resultado = productoService.obtenerProductoPorId(999L);

        // THEN - Verifico que devuelve null correctamente
        assertNull(resultado);
        verify(productoRepository, times(1)).findById(999L);
    }



    // TEST 5: Probar actualizarProducto() - caso exitoso
    // Verifica que actualice cuando el producto existe
    @Test
    void testActualizarProducto() {
        // GIVEN - Simulo que el producto existe y se puede actualizar
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // WHEN - Actualizo el producto
        Producto resultado = productoService.actualizarProducto(1L, producto);

        // THEN - Verifico que se actualizó correctamente
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        // Verifico que se hicieron ambas operaciones: verificar existencia y guardar
        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }



    // TEST 6: Probar actualizarProducto() - caso de producto no existente
    // Verifica que no actualice si el producto no existe
    @Test
    void testActualizarProductoNoExistente() {
        // GIVEN - Simulo que el producto NO existe
        when(productoRepository.existsById(999L)).thenReturn(false);

        // WHEN - Intento actualizar un producto inexistente
        Producto resultado = productoService.actualizarProducto(999L, producto);

        // THEN - Verifico que devuelve null y no intenta guardar
        assertNull(resultado);
        verify(productoRepository, times(1)).existsById(999L);
        // Verifico que NO se llamó a save() porque el producto no existe
        verify(productoRepository, never()).save(any(Producto.class));
    }



    // TEST 7: Probar eliminarProducto()
    // Verifica que delegate correctamente la eliminación al repository
    @Test
    void testEliminarProducto() {
        // WHEN - Elimino un producto (no necesito Given porque solo delega)
        productoService.eliminarProducto(1L);

        // THEN - Verifico que se llamó al repository para eliminar
        verify(productoRepository, times(1)).deleteById(1L);
    }    
    
    
    
    // TEST 8: Probar obtenerStockSeguro() - caso exitoso
    // Verifica la integración con el microservicio de inventario cuando funciona
    @Test
    void testObtenerStockSeguro() {
        // GIVEN - Simulo una respuesta exitosa del microservicio de inventario
        ProductoService.InventarioResponse inventarioResponse = new ProductoService.InventarioResponse();
        inventarioResponse.setIdProducto(1L);
        inventarioResponse.setStockActual(15);
        
        // Simulo que RestTemplate devuelve la respuesta exitosa
        when(restTemplate.getForObject(anyString(), eq(ProductoService.InventarioResponse.class)))
                .thenReturn(inventarioResponse);

        // WHEN - Obtengo el stock del producto
        Integer stock = productoService.obtenerStockSeguro(1L);

        // THEN - Verifico que devuelve el stock correcto
        assertEquals(15, stock);
        // Verifico que se hizo la llamada HTTP al microservicio
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductoService.InventarioResponse.class));
    }



    // TEST 9: Probar obtenerStockSeguro() - caso de error (Circuit Breaker)
    // Verifica que el sistema sea resiliente cuando el microservicio externo falla
    @Test
    void testObtenerStockSeguroConError() {
        // GIVEN - Simulo que el microservicio de inventario falla
        when(restTemplate.getForObject(anyString(), eq(ProductoService.InventarioResponse.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        // WHEN - Intento obtener stock a pesar del error
        Integer stock = productoService.obtenerStockSeguro(1L);

        // THEN - Verifico que implementa CIRCUIT BREAKER: devuelve 0 en lugar de fallar
        assertEquals(0, stock);  // Valor por defecto cuando hay error
        // Verifico que sí intentó hacer la llamada (pero falló)
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductoService.InventarioResponse.class));
    }



    // TEST 10: Probar obtenerProductoConStock() - integración completa
    // Verifica que combine correctamente datos locales + datos del microservicio externo
    @Test
    void testObtenerProductoConStock() {
        // GIVEN - Preparo ambas fuentes de datos
        // 1. Simulo que el producto existe en mi BD local
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        // 2. Simulo respuesta exitosa del microservicio de inventario
        ProductoService.InventarioResponse inventarioResponse = new ProductoService.InventarioResponse();
        inventarioResponse.setIdProducto(1L);
        inventarioResponse.setStockActual(20);
        
        when(restTemplate.getForObject(anyString(), eq(ProductoService.InventarioResponse.class)))
                .thenReturn(inventarioResponse);

        // WHEN - Obtengo el producto con stock
        ProductoConStockDTO resultado = productoService.obtenerProductoConStock(1L);

        // THEN - Verifico que combine correctamente ambas fuentes de datos
        assertNotNull(resultado);
        assertEquals("Laptop Dell", resultado.getNombre());  // Dato de mi BD
        assertEquals(20, resultado.getStock());               // Dato del microservicio externo
        // Verifico que consultó ambas fuentes
        verify(productoRepository, times(1)).findById(1L);
    }



    // TEST 11: Probar listarProductosConStock() - integración completa múltiple
    // Verifica que obtenga stock para cada producto de la lista
    @Test
    void testListarProductosConStock() {
        // GIVEN - Preparo datos para múltiples productos
        // 1. Mi BD local tiene una lista de productos
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);
        
        // 2. El microservicio responde con stock para cada producto
        ProductoService.InventarioResponse inventarioResponse = new ProductoService.InventarioResponse();
        inventarioResponse.setIdProducto(1L);
        inventarioResponse.setStockActual(25);
        
        when(restTemplate.getForObject(anyString(), eq(ProductoService.InventarioResponse.class)))
                .thenReturn(inventarioResponse);

        // WHEN - Obtengo la lista completa con stock
        List<ProductoConStockDTO> resultado = productoService.listarProductosConStock();

        // THEN - Verifico que cada producto tenga su stock correspondiente
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laptop Dell", resultado.get(0).getNombre());  // Dato local
        assertEquals(25, resultado.get(0).getStock());               // Dato externo
        // Verifico que consultó mi BD para obtener los productos
        verify(productoRepository, times(1)).findAll();
        
        // NOTA: Este test demuestra cómo el service orquesta múltiples fuentes de datos
        // para crear una respuesta completa al cliente
    }
}
