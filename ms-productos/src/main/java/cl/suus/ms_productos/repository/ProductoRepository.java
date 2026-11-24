package cl.suus.ms_productos.repository;

import cl.suus.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByIdAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    boolean existsByCategoriaId(Long categoriaId);

    @Query("""
            SELECT p FROM Producto p
            WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
              AND (:texto IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%')))
            ORDER BY p.id ASC
            """)
    List<Producto> buscarConFiltros(@Param("categoriaId") Long categoriaId,
                                    @Param("texto") String texto);

    @Query("SELECT p FROM Producto p WHERE p.precioOferta IS NOT NULL AND p.precioOferta < p.precio ORDER BY p.id ASC")
    List<Producto> findOfertas();

    @Query("SELECT p FROM Producto p WHERE p.stockCritico IS NOT NULL AND p.stock <= p.stockCritico ORDER BY p.stock ASC, p.id ASC")
    List<Producto> findCriticos();
}