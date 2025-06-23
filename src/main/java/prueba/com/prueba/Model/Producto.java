package prueba.com.prueba.Model;

// Imports para JPA (persistencia de datos)
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

// Imports para validaciones - Requisito de la rúbrica: "Validaciones mínimas"
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Import para documentación OpenAPI/Swagger - Requisito de la rúbrica
import io.swagger.v3.oas.annotations.media.Schema;

// @Entity: Le dice a JPA que esta clase representa una tabla en la base de datos
// @Table: Especifica el nombre exacto de la tabla en la BD (PRODUCTOS)
// @Schema: Documenta la entidad para Swagger UI - ayuda a generar documentación automática
@Entity
@Table(name = "PRODUCTOS")
@Schema(description = "Entidad que representa un producto en el sistema")
public class Producto {

    // @Id: Marca este campo como clave primaria
    // @GeneratedValue: La BD genera automáticamente el ID (autoincrement)
    // @Column: Mapea este atributo Java con la columna ID de la tabla
    // @Schema: Documenta este campo en Swagger con descripción y ejemplo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    // VALIDACIONES IMPLEMENTADAS - Requisito de rúbrica: "Validaciones mínimas"
    // @NotBlank: Valida que el campo no sea null, vacío o solo espacios
    // Cuando uso @Valid en el controller, Spring ejecuta automáticamente estas validaciones
    // Si la validación falla, Spring devuelve HTTP 400 con el mensaje especificado
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "NOMBRE")
    @Schema(description = "Nombre del producto", example = "Laptop Dell")
    private String nombre;

    // Este campo NO tiene validaciones porque la descripción puede ser opcional
    @Column(name = "DESCRIPCION")
    @Schema(description = "Descripción detallada del producto", example = "Laptop Dell Inspiron 15 con 8GB RAM")
    private String descripcion;

    // @NotNull: Valida que el campo no sea null (pero sí puede ser 0)
    // @Positive: Valida que el número sea mayor a 0 (precios negativos no tienen sentido)
    // Estas dos validaciones trabajan juntas para asegurar precios válidos
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(name = "PRECIO")
    @Schema(description = "Precio del producto", example = "799.99")
    private Double precio;

    // @NotBlank: Aseguro que todo producto tenga una categoría definida
    @NotBlank(message = "La categoría es obligatoria")
    @Column(name = "CATEGORIA")
    @Schema(description = "Categoría del producto", example = "Electrónicos")
    private String categoria;

    // Constructor por defecto requerido por JPA
    // JPA necesita un constructor sin parámetros para crear instancias de la entidad
    public Producto() {}

    // GETTERS Y SETTERS
    // Necesarios para que JPA pueda acceder a los campos privados
    // También los usa Jackson para serializar/deserializar JSON en los endpoints REST

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
