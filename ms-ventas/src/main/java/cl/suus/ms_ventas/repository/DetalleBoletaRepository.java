package cl.suus.ms_ventas.repository;

import cl.suus.ms_ventas.dto.ProductoMasVendidoResponse;
import cl.suus.ms_ventas.model.DetalleBoleta;
import cl.suus.ms_ventas.model.EstadoBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {

    @Query("""
            SELECT new cl.suus.ms_ventas.dto.ProductoMasVendidoResponse(
                d.productoId,
                d.nombreProducto,
                SUM(d.cantidad),
                SUM(d.subtotal)
            )
            FROM DetalleBoleta d
            WHERE d.boleta.estado <> :estadoExcluido
            GROUP BY d.productoId, d.nombreProducto
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<ProductoMasVendidoResponse> findMasVendidosExcluyendoEstado(@Param("estadoExcluido") EstadoBoleta estadoExcluido);
}