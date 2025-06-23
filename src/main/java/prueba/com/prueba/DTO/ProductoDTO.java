package prueba.com.prueba.DTO;

// Import para HATEOAS - Requisito de rúbrica: "Implementar HATEOAS"
import org.springframework.hateoas.RepresentationModel;

// DTO (Data Transfer Object) para transportar datos de productos
// HATEOAS: Extiende RepresentationModel para poder agregar enlaces (_links) 
// Esta clase define QUÉ datos del producto se envían al cliente (sin exponer la entidad JPA)
public class ProductoDTO extends RepresentationModel<ProductoDTO> {
    // Campos básicos del producto que se enviarán como JSON
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;

    // Constructor vacío requerido por Jackson para deserialización JSON
    public ProductoDTO() {}

    // Getters y setters necesarios para que Jackson serialice/deserialice JSON
    // También permite que Spring HATEOAS agregue enlaces a este DTO
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
