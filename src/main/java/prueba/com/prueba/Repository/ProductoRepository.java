package prueba.com.prueba.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prueba.com.prueba.Model.Producto;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Puedes agregar métodos personalizados aquí si los necesitas

    // Nuevo método para obtener todos los productos
    default List<Producto> obtenerTodos() {
        return findAll();
    }

    // Nuevo método para crear un producto
    default Producto crearNuevoProducto(Producto producto) {
        return save(producto);
    }
}