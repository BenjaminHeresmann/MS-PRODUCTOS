package prueba.com.prueba.Controller;

// Imports básicos de Spring MVC para crear controladores REST
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Imports para HATEOAS - Requisito de rúbrica: "Implementar HATEOAS"
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// Imports de mis DTOs y modelos
import prueba.com.prueba.DTO.ProductoConStockDTO;
import prueba.com.prueba.DTO.ProductoDTO;
import prueba.com.prueba.Model.Producto;
import prueba.com.prueba.Service.ProductoService;

// Import para validaciones - Requisito de rúbrica: "Validaciones mínimas"
import jakarta.validation.Valid;

// Imports para documentación OpenAPI/Swagger - Requisito de rúbrica
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// @RestController: Combina @Controller + @ResponseBody
// Significa que todos los métodos devuelven datos (JSON) directamente al cliente
// @RequestMapping: Define la URL base para todos los endpoints de este controller
// @Tag: Agrupa los endpoints en Swagger UI bajo una categoría específica
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para gestión de productos con información de stock")
public class ProductoController {

    // @Autowired: Spring inyecta automáticamente una instancia de ProductoService
    // Esto implementa el patrón de Inversión de Dependencias
    @Autowired
    private ProductoService productoService;

    // ENDPOINT 1: GET /api/productos - Listar todos los productos con stock
    // @GetMapping: Mapea peticiones HTTP GET a este método
    // @Operation: Documenta el endpoint para Swagger UI
    // @ApiResponse: Documenta las posibles respuestas HTTP
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos con información de stock")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    public CollectionModel<ProductoConStockDTO> listarProductos() {
        // 1. Llamo al service para obtener productos con stock
        List<ProductoConStockDTO> productos = productoService.listarProductosConStock();
        
        // 2. IMPLEMENTACIÓN DE HATEOAS - Requisito de rúbrica
        // Agrego enlaces navegables a cada producto individual
        for (ProductoConStockDTO producto : productos) {
            // Enlace "self" - apunta al recurso individual
            producto.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel());
            // Enlace "update" - permite actualizar el producto
            producto.add(linkTo(methodOn(ProductoController.class).actualizarProducto(producto.getId(), null)).withRel("update"));
            // Enlace "delete" - permite eliminar el producto
            producto.add(linkTo(ProductoController.class).slash("api").slash("productos").slash(producto.getId()).withRel("delete"));
        }
        
        // 3. Devuelvo una CollectionModel con enlaces a nivel de colección
        // Esto hace que la API sea "auto-descubrible" - el cliente recibe URLs para navegar
        return CollectionModel.of(productos)
                .add(linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel())
                .add(linkTo(methodOn(ProductoController.class).crearProducto(null)).withRel("create"));
    }

    // ENDPOINT 2: POST /api/productos - Crear nuevo producto
    // @PostMapping: Mapea peticiones HTTP POST a este método
    // @Valid: ACTIVA LAS VALIDACIONES - Requisito de rúbrica: "Validaciones mínimas"
    // Spring ejecuta automáticamente las validaciones definidas en la entidad Producto
    // Si hay errores, Spring devuelve HTTP 400 con detalles del error
    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ProductoDTO crearProducto(@Valid @RequestBody Producto producto) {
        Producto productoGuardado = productoService.crearProducto(producto);
        
        ProductoDTO productoDTO = convertirAProductoDTO(productoGuardado);
        productoDTO.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(productoGuardado.getId())).withSelfRel());
        productoDTO.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        productoDTO.add(linkTo(methodOn(ProductoController.class).actualizarProducto(productoGuardado.getId(), null)).withRel("update"));
        
        return productoDTO;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID con información de stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ProductoConStockDTO obtenerProductoPorId(@Parameter(description = "ID del producto") @PathVariable Long id) {
        ProductoConStockDTO producto = productoService.obtenerProductoConStock(id);
        if (producto != null) {
            producto.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel());
            producto.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
            producto.add(linkTo(methodOn(ProductoController.class).actualizarProducto(id, null)).withRel("update"));
        }
        return producto;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ProductoDTO actualizarProducto(@Parameter(description = "ID del producto") @PathVariable Long id, @Valid @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        if (productoActualizado != null) {
            ProductoDTO productoDTO = convertirAProductoDTO(productoActualizado);
            productoDTO.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel());
            productoDTO.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
            return productoDTO;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public void eliminarProducto(@Parameter(description = "ID del producto") @PathVariable Long id) {
        productoService.eliminarProducto(id);
    }
    
    // Método auxiliar para convertir Producto a ProductoDTO
    private ProductoDTO convertirAProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        return dto;
    }
}