package prueba.com.prueba.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import prueba.com.prueba.DTO.ProductoConStockDTO;
import prueba.com.prueba.Model.Producto;
import prueba.com.prueba.Repository.ProductoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto obtenerProductoPorId(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.orElse(null);
    }

    public Producto actualizarProducto(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setId(id);
            return productoRepository.save(producto);
        }
        return null;
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public ProductoConStockDTO obtenerProductoConStock(Long id) {
        Producto producto = obtenerProductoPorId(id);
        if (producto == null) return null;
        Integer stock = obtenerStockSeguro(id);

        ProductoConStockDTO dto = new ProductoConStockDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        dto.setStock(stock);
        return dto;
    }

    public List<ProductoConStockDTO> listarProductosConStock() {
        List<Producto> productos = listarProductos();
        List<ProductoConStockDTO> lista = new ArrayList<>();
        for (Producto producto : productos) {
            Integer stock = obtenerStockSeguro(producto.getId());
            ProductoConStockDTO dto = new ProductoConStockDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setCategoria(producto.getCategoria());
            dto.setStock(stock);
            lista.add(dto);
        }
        return lista;
    }

    public Integer obtenerStockSeguro(Long productoId) {
        try {
            String url = "http://localhost:8085/inventario/" + productoId;
            InventarioResponse inventario = restTemplate.getForObject(url, InventarioResponse.class);
            return inventario != null ? inventario.getStockActual() : 0;
        } catch (RestClientException e) {
            return 0;
        }
    }

    public static class InventarioResponse {
        private Long idProducto;
        private Integer stockActual;

        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
        public Integer getStockActual() { return stockActual; }
        public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    }
}