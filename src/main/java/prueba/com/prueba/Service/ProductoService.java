package prueba.com.prueba.Service;

// Imports de Spring para inyección de dependencias y servicios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Import para comunicación con microservicios externos
import org.springframework.web.client.RestTemplate;

// Imports de mis clases del proyecto
import prueba.com.prueba.DTO.ProductoConStockDTO;
import prueba.com.prueba.Model.Producto;
import prueba.com.prueba.Repository.ProductoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Service: Marca esta clase como un servicio de Spring
// Spring la detecta automáticamente y la registra como bean
// Aquí va toda la LÓGICA DE NEGOCIO del microservicio
@Service
public class ProductoService {

    // @Autowired: Spring inyecta automáticamente el repository
    // Esto implementa el patrón Repository para acceso a datos
    @Autowired
    private ProductoRepository productoRepository;

    // @Autowired: Spring inyecta el RestTemplate configurado en RestTemplateConfig
    // Lo uso para hacer llamadas HTTP a otros microservicios
    @Autowired
    private RestTemplate restTemplate;

    // @Value: Inyecta el valor desde application.properties
    // Esto permite configurar la URL del microservicio de inventario por ambiente
    // En dev puede ser localhost:8085, en prod puede ser una URL diferente
    @Value("${inventario.service.url}")
    private String inventarioServiceUrl;

    // MÉTODO BÁSICO: Obtener todos los productos (sin stock)
    // Simplemente delega al repository que hace la query a la BD
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // MÉTODO BÁSICO: Crear nuevo producto
    // JPA/Hibernate genera automáticamente el INSERT SQL
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // MÉTODO BÁSICO: Buscar producto por ID
    // findById devuelve Optional<Producto> para manejar casos donde no existe
    public Producto obtenerProductoPorId(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.orElse(null);
    }

    // MÉTODO BÁSICO: Actualizar producto existente
    // Primero verifico que existe, luego actualizo
    public Producto actualizarProducto(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setId(id);  // Aseguro que mantenga el mismo ID
            return productoRepository.save(producto);  // save() hace UPDATE si el ID existe
        }
        return null;
    }

    // MÉTODO BÁSICO: Eliminar producto
    // JPA/Hibernate genera automáticamente el DELETE SQL
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // MÉTODO AVANZADO: Obtener UN producto con información de stock
    // Este método combina datos de MI base de datos + datos del microservicio de inventario
    public ProductoConStockDTO obtenerProductoConStock(Long id) {
        // 1. Obtengo el producto de mi BD local
        Producto producto = obtenerProductoPorId(id);
        if (producto == null) return null;
        
        // 2. Hago llamada al microservicio externo para obtener el stock
        Integer stock = obtenerStockSeguro(id);

        // 3. Combino ambos datos en un DTO
        ProductoConStockDTO dto = new ProductoConStockDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        dto.setStock(stock);  // Este dato viene del microservicio externo
        return dto;
    }

    // MÉTODO AVANZADO: Listar TODOS los productos con información de stock
    // Para cada producto, hago una llamada al microservicio de inventario
    public List<ProductoConStockDTO> listarProductosConStock() {
        // 1. Obtengo todos los productos de mi BD
        List<Producto> productos = listarProductos();
        List<ProductoConStockDTO> lista = new ArrayList<>();
        
        // 2. Para cada producto, obtengo su stock del microservicio externo
        for (Producto producto : productos) {
            Integer stock = obtenerStockSeguro(producto.getId());  // Llamada externa
            
            // 3. Creo un DTO combinando datos locales + externos
            ProductoConStockDTO dto = new ProductoConStockDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setCategoria(producto.getCategoria());
            dto.setStock(stock);  // Dato externo
            lista.add(dto);
        }
        return lista;
    }

    // MÉTODO CRÍTICO: Comunicación segura con microservicio externo
    // Este método implementa el patrón CIRCUIT BREAKER básico
    // Si el servicio de inventario falla, MI API sigue funcionando
    public Integer obtenerStockSeguro(Long productoId) {
        try {
            // 1. Construyo la URL del microservicio externo
            String url = inventarioServiceUrl + "/inventario/" + productoId;
            
            // 2. Hago la llamada HTTP GET usando RestTemplate
            InventarioResponse inventario = restTemplate.getForObject(url, InventarioResponse.class);
            
            // 3. Si recibo respuesta válida, devuelvo el stock
            return inventario != null ? inventario.getStockActual() : 0;
            
        } catch (Exception e) {
            // 4. CIRCUIT BREAKER: Si hay cualquier error (timeout, servicio caído, etc.)
            // NO fallo completamente, sino que devuelvo un valor por defecto
            // Esto garantiza que MI microservicio siga funcionando aunque el de inventario falle
            System.out.println("Error al consultar inventario para producto " + productoId + ": " + e.getMessage());
            return 0;  // Stock por defecto en caso de error
        }
    }

    // CLASE INTERNA: DTO para deserializar la respuesta del microservicio de inventario
    // Esta clase mapea exactamente el JSON que devuelve el servicio de inventario
    // Es estática porque no necesita acceso a instancias de ProductoService
    public static class InventarioResponse {
        private Long idProducto;
        private Integer stockActual;

        // Getters y setters necesarios para que Jackson pueda deserializar el JSON
        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
        public Integer getStockActual() { return stockActual; }
        public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    }
}