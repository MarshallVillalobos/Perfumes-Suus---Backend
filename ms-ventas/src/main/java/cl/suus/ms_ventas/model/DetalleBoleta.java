package cl.suus.ms_ventas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_boleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boleta_id", nullable = false)
    private Boleta boleta;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Integer subtotal;
}